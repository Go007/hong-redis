package com.hong.controller;

import com.hong.common.bean.Result;
import com.hong.entity.User;
import com.hong.lock.DistributedLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("redis")
public class RedisController {

    private final Logger logger = LoggerFactory.getLogger(RedisController.class);

    @Resource
    private RedisTemplate<String, User> redisTemplate;

    @Resource
    private DistributedLock redisDistributedLock;

    private static Long count = 0L;

    @RequestMapping("user/{id}")
    public Result save(@PathVariable long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("测试");
        user.setPassword(123L);
        redisTemplate.opsForValue().set(id + "", user, 60, TimeUnit.SECONDS);
        return Result.buildSuccess();
    }

    @RequestMapping("{key}")
    public Result get(@PathVariable String key) {
        User user = redisTemplate.opsForValue().get(key);
        Result result = new Result();
        result.setData(user);
        return result;
    }

    @GetMapping("/testLock")
    public Result testLock() {
        Result result = new Result();
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
                            boolean res = redisDistributedLock.lock("lockKey", 30, 0, 0);
                            logger.info(res ? "get lock success : " + "lockKey" : "get lock failed : " + "lockKey");
                            count++;
                            countDownLatch2.countDown();
                        }
                );
                countDownLatch.countDown();
            }
            // 保证所有线程执行完
            countDownLatch2.await();
            executorService.shutdown();
            result.setData(count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // redisDistributedLock.releaseLock("lockKey");
            logger.info("release lock : " + "lockKey");
        }
        return result;
    }

}
