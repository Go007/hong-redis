package com.hong.lock;

import com.hong.config.RedissonConnector;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <br>Ridisson实现的Redis分布式锁</br>
 */
@Component
public class RedissonLocker {

    private final Logger logger = LoggerFactory.getLogger(RedissonLocker.class);

    private final static String LOCKER_PREFIX = "lock:";

    // 锁的有效时间
    private final static long LOCKER_TIME = 30000;

    // 获取锁最长等待时间
    private final static long WAIT_TIME = 30;

    @Autowired
    RedissonConnector redissonConnector;

    public <T> T lock(String resourceName, RedisLockWorker<T> worker) {
        return lock(resourceName, worker, LOCKER_TIME);
    }

    public <T> T lock(String resourceName, RedisLockWorker<T> worker, long lockTime) {
        RedissonClient redisson = redissonConnector.getClient();
        RLock lock = redisson.getLock(LOCKER_PREFIX + resourceName);
        boolean success;
        try {
            success = lock.tryLock(WAIT_TIME, lockTime, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            logger.error("Redisson获取锁[{key}]失败", resourceName);
            return null;
        }
        if (success) {
            try {
                return worker.invokeAfterLockAquired();
            } catch (Exception e) {
                 logger.error("获取锁[{key}]成功,但竞争逻辑执行发生错误",resourceName);
            } finally {
                lock.unlock();
            }
        }
        return null;
    }
}
