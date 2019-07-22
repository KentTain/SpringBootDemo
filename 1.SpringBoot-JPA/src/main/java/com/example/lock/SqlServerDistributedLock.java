package com.example.lock;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.DateExtensions;
import com.example.GlobalConfig;
import com.example.TimeSpan;



/**
 * Sql Server的分布式锁，使用：dbo.sp_getapplock、dbo.sp_releaseapplock
 * 
 * @author 田长军
 * 
 */
public class SqlServerDistributedLock extends DistributedLockAbstract<Boolean> {

	/**
	 * SqlServer的分布式锁
	 */
	@Override
	public void DoDistributedLock(String key, TimeSpan acquireTimeout, TimeSpan lockTimeOut, Consumer<Boolean> action) {
		// TODO Auto-generated method stub
		key = DistributedLockPro + key;
		java.util.UUID.randomUUID().toString();
		String connectionString = GlobalConfig.DatabaseConnectionString;

		Connection connection = getConnection(connectionString);

		long nowTicks = DateExtensions.getTicks(new Date());
		long end = nowTicks + acquireTimeout.Ticks();
		while (nowTicks < end) {
			boolean result = executeAcquireCommand(connection, key, Mode.Exclusive, acquireTimeout.TotalMilliseconds());
			if (result) {
				try {
					System.out.println(
							String.format("---begin-Acquire-nowTicks: %s, end: %s, timeoutmilliseconds: %s, result: %s",
									nowTicks, end, acquireTimeout.TotalMilliseconds(), result));
					action.accept(result);
					System.out.println(
							String.format("---end-Acquire-nowTicks: %s, end: %s, timeoutmilliseconds: %s, result: %s",
									nowTicks, end, acquireTimeout.TotalMilliseconds(), result));
				} finally {
					System.out.println(
							String.format("---begin-Release-nowTicks: %s, end: %s, timeoutmilliseconds: %s, result: %s",
									nowTicks, end, acquireTimeout.TotalMilliseconds(), result));
					executeReleaseCommand(connection, key);
					System.out.println(
							String.format("---end-Release-nowTicks: %s, end: %s, timeoutmilliseconds: %s, result: %s",
									nowTicks, end, acquireTimeout.TotalMilliseconds(), result));
				}

				return;
			}

			try {
				Thread.sleep(10);
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private Connection getConnection(String connectionString) {
		try {
			String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; // 加载JDBC驱动
			String userName = "sa"; // 默认用户名
			String userPwd = "P@ssw0rd"; // 密码
			//DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
			Class.forName(driverName);
			return DriverManager.getConnection(connectionString, userName, userPwd);
		} catch (Exception ex) {
			Logger.getLogger(SqlServerDistributedLock.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	private boolean executeAcquireCommand(Connection conn, String lockName, Mode exclusive, int timeoutMillis) {
		if (conn == null) {
			return false;
		}
		CallableStatement sp = null;
		try {
			sp = conn.prepareCall("{?=call dbo.sp_getapplock(?,?,?,?,?)}");
			sp.registerOutParameter(1, Types.INTEGER);
			sp.setString(2, lockName); // @Resource
			sp.setString(3, exclusive.toString()); // @LockMode: Shared、Update、Exclusive、IntentExclusive、IntentShared
			sp.setString(4, "Session"); // @LockOwner: "Transaction" or Session
			sp.setInt(5, timeoutMillis); // @LockTimeout
			sp.setString(6, "public"); // @DbPrincipal

			sp.execute();

			int result = sp.getInt(1);
			return parseExitCode(result);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close(sp);
		}
	}

	private boolean executeReleaseCommand(Connection conn, String lockName) {
		if (conn == null) {
			return false;
		}
		CallableStatement sp = null;
		try {
			sp = conn.prepareCall("{?=call dbo.sp_releaseapplock(?,?,?)}");
			sp.registerOutParameter(1, Types.INTEGER);
			sp.setString(2, lockName); // @Resource
			sp.setString(3, "Session"); // @LockOwner
			sp.setString(4, "public"); // @DbPrincipal

			sp.execute();

			int result = sp.getInt(1);
			return parseExitCode(result);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close(sp);
		}
	}

	private boolean parseExitCode(int exitCode) throws Exception {
		// sp_getapplock exit codes documented at
		// https://msdn.microsoft.com/en-us/library/ms189823.aspx

		switch (exitCode) {
		case 0:
		case 1:
			return true;

		case -1:
			return false;

		case -2: // canceled
			throw new Exception(GetErrorMessage(exitCode, "canceled"));
		case -3: // deadlock
			throw new Exception(GetErrorMessage(exitCode, "deadlock"));
		case -999: // parameter / unknown
			throw new Exception(GetErrorMessage(exitCode, "parameter validation or other error"));

		default:
			if (exitCode <= 0)
				throw new Exception(GetErrorMessage(exitCode, "unknown"));
			return true; // unknown success code
		}
	}

	private String GetErrorMessage(int exitCode, String type) {
		return String.format("The request for the distribute lock failed with exit code %s (%s)", exitCode, type);
	}

	private enum Mode {
		Shared, Update, Exclusive,
	}

	private static void close(Object obj) {
		if (obj == null) {
			return;
		}
		try {
			if (obj instanceof Statement) {
				((Statement) obj).close();
			} else if (obj instanceof PreparedStatement) {
				((PreparedStatement) obj).close();
			} else if (obj instanceof ResultSet) {
				((ResultSet) obj).close();
			} else if (obj instanceof Connection) {
				((Connection) obj).close();
			}
		} catch (SQLException ex) {
			Logger.getLogger(SqlServerDistributedLock.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}