package com.example.lock;

import java.util.function.Consumer;

import com.example.TimeSpan;

public abstract class DistributedLockAbstract<T> implements IDistributedLock<T> {
	
	protected TimeSpan DefaultAcuireTimeOut = new TimeSpan(0, 0, 0, 60, 0);
	protected TimeSpan DefaultLockTimeOut = new TimeSpan(0, 0, 15, 0, 0);
	protected static String DistributedLockPro = "D_Lock_";

    public void DoDistributedLock(String key, Consumer<T> action)
    {
        DoDistributedLock(key, DefaultAcuireTimeOut, DefaultLockTimeOut, action);
    }
    public void DoDistributedLock(String key, TimeSpan acquireTimeout, Consumer<T> action)
    {
        DoDistributedLock(key, acquireTimeout, DefaultLockTimeOut, action);
    }
    public abstract void  DoDistributedLock(String key, TimeSpan acquireTimeout, TimeSpan lockTimeOut, Consumer<T> action);
}
