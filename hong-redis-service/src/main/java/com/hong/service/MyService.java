package com.hong.service;

import com.hong.annotations.LockAction;
import com.hong.lock.DistributedLock;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MyService {

    @Resource
    private DistributedLock redisDistributedLock;

    private static Long count = 0L;

    public Long testCurrency() {
        //使用CountDownLatch模拟高并发场景,测试Redis分布式锁
        // 相当于计数器，当所有都准备好了，再一起执行，模仿多并发，保证并发量
        final CountDownLatch countDownLatch = new CountDownLatch(100);
        // 保证所有线程执行完了再打印atomicInteger的值
        final CountDownLatch countDownLatch2 = new CountDownLatch(100);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try {
            for (int i = 0; i < 100; i++) {
                executorService.submit(
                        () -> {
                            try {
                                //一直阻塞当前线程，直到计时器的值为0,保证同时并发
                                countDownLatch.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            /*redisDistributedLock.lock("lockKey", 20, 0, 0);
                            count++;*/

                            countAdder();

                            countDownLatch2.countDown();
                        }
                );
                countDownLatch.countDown();
            }
            // 保证所有线程执行完
            countDownLatch2.await();
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisDistributedLock.releaseLock("lockKey");
        }

        return count;
    }

    @LockAction("lockKey")
    public void countAdder() {
        count++;
    }
}
