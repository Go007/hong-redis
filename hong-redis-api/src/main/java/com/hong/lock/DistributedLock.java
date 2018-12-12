package com.hong.lock;

/**
 * Redis分布式锁的顶层接口类
 * 可以扩展该接口使用其他方式来实现分布式锁
 */
public interface DistributedLock {

    static final long TIMEOUT_MILLIS = 30000;

    static final int RETRY_TIMES = Integer.MAX_VALUE;

    static final long SLEEP_MILLIS = 500;

    boolean lock(String key);

    boolean lock(String key, int retryTimes);

    boolean lock(String key, int retryTimes, long sleepMillis);

    boolean lock(String key, long expire);

    boolean lock(String key, long expire, int retryTimes);

    boolean lock(String key, long expire, int retryTimes, long sleepMillis);

    boolean releaseLock(String key);
}
