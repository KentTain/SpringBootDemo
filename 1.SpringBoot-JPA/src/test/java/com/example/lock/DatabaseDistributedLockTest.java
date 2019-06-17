package com.example.lock;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.GlobalConfig;

import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

public class DatabaseDistributedLockTest {
	private Logger logger = LoggerFactory.getLogger(DatabaseDistributedLockTest.class);
	private int n = 100;// 库存：500
	private int icount = 100; // 线程数

	@Before
	public void setUp() throws ParseException {
		GlobalConfig.DatabaseConnectionString = "jdbc:sqlserver://127.0.0.1;databaseName=sm_project";
		GlobalConfig.MySqlConnectionString = "jdbc:mysql://localhost:3306/sm_project?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true";
	}

	private class NoDistributedLock_SecKill extends TestRunnable {
		@Override
		public void runTest() throws Throwable {
			SecKill("NoDistributedLock_SecKill_Test", SecList.NoKillSet);
		}
	}
	@Test
	public void test_NoDistributedLock_SecKill() throws Throwable {
		n = 100;// 库存：100
		String except = java.util.stream.IntStream.range(0, n).boxed().map(i -> (n - i.intValue() - 1))
				.map(i -> i.toString()).collect(Collectors.joining(","));

		// Runner数组，想当于并发多少个。
		TestRunnable[] trs = new TestRunnable[icount];
		for (int i = 0; i < icount; i++) {
			trs[i] = new NoDistributedLock_SecKill();
		}

		// 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		// 开发并发执行数组里定义的内容
		mttr.runTestRunnables();

		String result = toCommaSeparatedInt(SecList.NoKillSet);

		System.out.println("---except: " + except);
		System.out.println("---result: " + result);

		Assert.assertNotEquals(except, result);
	}

	private class SqlServerDistributedLock_SecKill extends TestRunnable {
		@Override
		public void runTest() throws Throwable {
			new SqlServerDistributedLock().DoDistributedLock("SqlServerDistributedLock_SecKill", (success) -> {
				SecKill("SqlServerDistributedLock_SecKill", SecList.SqlServerSecKillSet);
			});
		}
	}
	@Test
	public void test_SqlServerDistributedLock_SecKill() throws Throwable {
		n = 100;// 库存：500
		String except = java.util.stream.IntStream.range(0, n).boxed().map(i -> (n - i.intValue() - 1))
				.map(i -> i.toString()).collect(Collectors.joining(","));

		// Runner数组，想当于并发多少个。
		TestRunnable[] trs = new TestRunnable[icount];
		for (int i = 0; i < icount; i++) {
			trs[i] = new SqlServerDistributedLock_SecKill();
		}

		// 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		// 开发并发执行数组里定义的内容
		mttr.runTestRunnables();

		String result = toCommaSeparatedInt(SecList.SqlServerSecKillSet);

		System.out.println("---except: " + except);
		System.out.println("---result: " + result);

		Assert.assertEquals(except, result);
	}

	private class MySqlDistributedLock_SecKill extends TestRunnable {
		@Override
		public void runTest() throws Throwable {
			MySqlDistributedLock.getInstance().DoDistributedLock("MySqlDistributedLock_SecKill", (success) -> {
				SecKill("MySqlDistributedLock_SecKill", SecList.MySqlSecKillSet);
			});
		}
	}
	@Test
	public void test_MySqlDistributedLock_SecKill() throws Throwable {
		n = 100;// 库存：500
		String except = java.util.stream.IntStream.range(0, n).boxed().map(i -> (n - i.intValue() - 1))
				.map(i -> i.toString()).collect(Collectors.joining(","));

		// Runner数组，想当于并发多少个。
		TestRunnable[] trs = new TestRunnable[icount];
		for (int i = 0; i < icount; i++) {
			trs[i] = new MySqlDistributedLock_SecKill();
		}

		// 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		// 开发并发执行数组里定义的内容
		mttr.runTestRunnables();

		String result = toCommaSeparatedInt(SecList.MySqlSecKillSet);

		System.out.println("---except: " + except);
		System.out.println("---result: " + result);

		Assert.assertEquals(except, result);
	}
	
	public static class SecList {
		public static final List<Integer> NoKillSet = new ArrayList<Integer>();

		public static final List<Integer> RedisSecKillSet = new ArrayList<Integer>();

		public static final List<Integer> SqlServerSecKillSet = new ArrayList<Integer>();

		public static final List<Integer> MySqlSecKillSet = new ArrayList<Integer>();

	}

	/**
	 * 秒杀
	 * 
	 * @param name
	 * @param left
	 */
	private void SecKill(String name, List<Integer> left) {
		try {

			System.out.println(String.format("Thread[%s--%s]获得了锁，剩余库存：%s", name, Thread.currentThread().getId(), // 异步下抛出异常
					--n));

			left.add(n);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
	}

	/**
	 * 将一个int列表，转换成：1, 2, 3
	 * 
	 * @param enumeration 需要转换的列表
	 * @return String
	 */
	private static String toCommaSeparatedInt(List<Integer> enumeration) {
		if (enumeration == null || enumeration.size() == 0)
			return "";

		Function<Integer, String> selector = s -> s.toString();
		return enumeration.stream().map(selector).collect(Collectors.joining(","));
	}

}
