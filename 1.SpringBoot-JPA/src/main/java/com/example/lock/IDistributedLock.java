package com.example.lock;

import java.util.function.Consumer;

import com.example.TimeSpan;


public interface IDistributedLock<T> {
	/**
	 * 分布式锁
	 * 
	 * @param key    锁的key
	 * @param action 需要上锁的方法
	 */
	void DoDistributedLock(String key, Consumer<T> action);

	/**
	 * 分布式锁
	 * 
	 * @param key            锁的key
	 * @param acquireTimeout 获取Key的超时时间
	 * @param action         需要上锁的方法
	 */
	void DoDistributedLock(String key, TimeSpan acquireTimeout, Consumer<T> action);

	/**
	 * 分布式锁
	 * 
	 * @param key            锁的key
	 * @param acquireTimeout 获取Key的超时时间
	 * @param lockTimeOut    锁的过期时间
	 * @param action         需要上锁的方法
	 */
	void DoDistributedLock(String key, TimeSpan acquireTimeout, TimeSpan lockTimeOut, Consumer<T> action);
}
