package com.example.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CountDownTest {
	private Logger logger = LoggerFactory.getLogger(DatabaseDistributedLockTest.class);
	
	private static Integer count = 1000;
	private CountDownLatch countDownLatch;
	private ExecutorService service;
	
	@Before
    public void prepareData() {
        countDownLatch = new CountDownLatch(10);
        service = Executors.newFixedThreadPool(10);
    }
	
	@Test
    public void excutorTest() {
        for (int i = 0; i < 100; i++) {
            service.execute(() -> {
                try {
                    Thread.sleep(100);
                    System.out.println(Thread.currentThread().getId() + ", 我阻塞了100ms");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        service.shutdown();
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }
	
	@Test
	public void runCountDown() {
		for (int i = 0; i < 100; i++) {
			service.execute(new RunableTask(countDownLatch));
		}
		try {
			countDownLatch.await();
			//logger.info("执行成功");
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error("抛出中断异常", e);
		}
	}

	public class RunableTask implements Runnable {
		private CountDownLatch countDownLatch;

		public RunableTask(CountDownLatch countDownLatch) {
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(100);
				//System.out.println(Thread.currentThread().getId() + ", 我阻塞了100ms");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				countDownLatch.countDown();
			}
		}
	}

	
}
