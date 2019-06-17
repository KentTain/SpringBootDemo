package com.example.lock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Date;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.DatabaseType;
import com.example.DateExtensions;
import com.example.GlobalConfig;
import com.example.TimeSpan;

/**
 * MySql的分布式锁，使用数据库的行级锁--表：dbo.sys_DatabaseDistributedLock
 * 
 * @author 田长军
 * 
 */
public class MySqlDistributedLock extends DistributedLockAbstract<Boolean> {
	public static final String DefaultDbo = "dbo";
	public static final String LockTableName = "sys_DatabaseDistributedLock";

	public static final DatabaseType dbType = DatabaseType.MySql;
	private static final String connectionString = GlobalConfig.MySqlConnectionString;

	private static class SingletonHolder{
        private static MySqlDistributedLock instance=new MySqlDistributedLock();
    }
    private MySqlDistributedLock(){
    	Connection connection = null;
		try {
			connection = getConnection(connectionString);
			// 创建锁表及插入行
			String createTableSql = GetCreateLockTableSql();

			System.out.println(createTableSql);
			executeCommand(connection, createTableSql);
		} catch (Exception e) {
			Thread.currentThread().interrupt();
		} finally {
			close(connection);
		}
    }
    public static MySqlDistributedLock getInstance(){
    	return SingletonHolder.instance;
    }
	
	/**
	 * MySql的分布式锁
	 */
	@Override
	public void DoDistributedLock(String key, TimeSpan acquireTimeout, TimeSpan lockTimeOut, Consumer<Boolean> action) {
		// TODO Auto-generated method stub
		key = DistributedLockPro + key;
		String value = java.util.UUID.randomUUID().toString();

		Connection connection = getConnection(connectionString);

		// 创建锁表及插入行
		String insertDataSql = InsertLockKey(key, value);
		System.out.println(String.format("---begin-Insert-lock-sql: %s", insertDataSql));
		executeCommand(connection, insertDataSql);
		System.out.println(String.format("---end-Insert-lock-sql: %s", insertDataSql));
		
		Statement statement = null;
		try {
			connection.setAutoCommit(false);
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			String cmdText = GetLockRowSql(key);
			System.out.println(String.format("---begin-get-cmdText: %s", cmdText));
			long nowTicks = DateExtensions.getTicks(new Date());
			long end = nowTicks + acquireTimeout.Ticks();
			while (nowTicks < end) {
				nowTicks = DateExtensions.getTicks(new Date());
				boolean result = statement.execute(cmdText);
				System.out.println(
						String.format("----nowTicks: %s, end: %s, timeoutmilliseconds: %s, result: %s",
								nowTicks, end, acquireTimeout.TotalMilliseconds(), result));
				if (result) {
					try {
						action.accept(result);
					} finally {
						System.out.println(
								String.format("---begin-Release-nowTicks: %s, end: %s, timeoutmilliseconds: %s, result: %s",
										nowTicks, end, acquireTimeout.TotalMilliseconds(), result));
						connection.commit();
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

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				close(statement);
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static Connection getConnection(String connectionString) {
		try {
			String userName = "sa"; // 默认用户名
			String userPwd = "P@ssw0rd"; // 密码

			switch (dbType) {
			case MySql:
				userName = "root";
				userPwd = "P@ssw0rd";
				Class.forName("com.mysql.cj.jdbc.Driver");
				// DriverManager.registerDriver(new com.mysql.jdbc.Driver());
				break;
			case SqlServer:
				userName = "sa";
				userPwd = "P@ssw0rd";
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				// DriverManager.registerDriver(new
				// com.microsoft.sqlserver.jdbc.SQLServerDriver());
				break;
			}
			return DriverManager.getConnection(connectionString, userName, userPwd);
		} catch (Exception ex) {
			Logger.getLogger(MySqlDistributedLock.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	private static String GetCreateLockTableSql() {
		String sql = "";
		switch (dbType) {
		case MySql:
			sql = String.format("CREATE TABLE IF NOT EXISTS `%s.%s` (\r\n"
					+ "`Id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',\r\n"
					+ "`LockKey` varchar(64) NOT NULL DEFAULT '' COMMENT '锁定的方法名',\r\n"
					+ "`Desc` varchar(1024) NOT NULL DEFAULT '备注信息',\r\n"
					+ "`UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '保存数据时自动生成',\r\n"
					+ "PRIMARY KEY (`id`),\r\n" + "UNIQUE KEY `IX_%s_LockKey` (`LockKey`) USING BTREE\r\n"
					+ "              ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_general_ci COMMENT='锁定中的方法';",
					DefaultDbo, LockTableName, LockTableName);
			break;
		default:
			sql = String.format("\r\n" + "		IF (NOT EXISTS (SELECT * \r\n" + "FROM INFORMATION_SCHEMA.TABLES \r\n"
					+ "WHERE TABLE_SCHEMA = '%s' \r\n" + "AND  TABLE_NAME = '%s'))\r\n" + "   BEGIN\r\n"
					+ "        CREATE TABLE [%s].[%s](\r\n" + "           [Id] [int] IDENTITY(1,1) NOT NULL,\r\n"
					+ "           [LockKey] [nvarchar](64) NULL,\r\n"
					+ "           [Desc] [nvarchar](1024) NULL,\r\n" + "           [UpdateTime] [datetime] NULL,\r\n"
					+ "        CONSTRAINT [IX_%s_LockKey] UNIQUE ([LockKey]),\r\n"
					+ "        CONSTRAINT [PK_%s] PRIMARY KEY CLUSTERED,\r\n" + "       (\r\n"
					+ "           [Id] ASC\r\n"
					+ "       )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]\r\n"
					+ "       ) ON [PRIMARY]\r\n" + "   END", DefaultDbo, LockTableName, DefaultDbo, LockTableName, LockTableName, LockTableName);
			break;
		}

		return sql;
	}

	private String InsertLockKey(String locakKey, String value) {
		String sql = "";
		switch (dbType) {
		case MySql:
			sql = String.format(
					"INSERT INTO `%s.%s` (`LockKey`,`Desc`,`UpdateTime`)"
							+ "VALUES ('%s','%s', UTC_TIMESTAMP())ON DUPLICATE KEY UPDATE  LockKey=VALUES(LockKey)",
					DefaultDbo, LockTableName, locakKey, value);
			break;
		default:
			sql = String.format(
					"IF NOT EXISTS (SELECT * FROM [%s].[%s] WHERE [LockKey] = '%s')\r\n" + "      BEGIN\r\n"
							+ "        INSERT INTO [%s].[%s] ([LockKey],[Desc],[UpdateTime])\r\n"
							+ "        VALUES ('%s','%s', getutcdate())\r\n" + "      END",
					DefaultDbo, LockTableName, locakKey, value);
			break;
		}

		return sql;
	}

	private String GetLockRowSql(String locakKey) {
		String sql = "";
		switch (dbType) {
		case MySql:
			sql = String.format("select `Id` from `%s.%s` where `LockKey`='%s' for update", DefaultDbo, LockTableName,
					locakKey);
			break;
		default:
			sql = String.format("select [Id] from [%s].[%s] WITH (ROWLOCK,XLOCK) where [LockKey]=N'%s'", DefaultDbo,
					LockTableName, locakKey);
			break;
		}

		return sql;
	}

	private static boolean executeCommand(Connection conn, String cmdText) {
		if (conn == null) {
			return false;
		}

		Statement statement = null;
		try {
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			if (statement == null) {
				return false;
			}

			int i = statement.executeUpdate(cmdText);
			return true;
		} catch (Exception ex) {
			Logger.getLogger(MySqlDistributedLock.class.getName()).log(Level.SEVERE, null, ex);
			ex.printStackTrace();

			return false;
		} finally {
			close(statement);
		}
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
			Logger.getLogger(MySqlDistributedLock.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}