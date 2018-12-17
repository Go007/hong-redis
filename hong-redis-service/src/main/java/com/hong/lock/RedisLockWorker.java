package com.hong.lock;

/**
 * Redisson分布式锁对竞争互斥业务逻辑的抽象
 * @param <T>
 */
@FunctionalInterface
public interface RedisLockWorker<T> {
	
	T invokeAfterLockAquired() throws Exception;

}
