package com.hong;

import com.hong.service.RedisService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisService redisService;

    @Test
    public void test() throws Exception {
        stringRedisTemplate.opsForValue().set("aaa", "111");
        System.out.println(stringRedisTemplate.opsForValue().get("aaa"));
        // Assert.assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));

        redisService.set("bbb", "222");
        System.out.println(redisService.getStr("bbb"));
    }

    long timed = 0L;

    @Before
    public void start() {
        System.out.println("开始测试");
        timed = System.currentTimeMillis();
    }

    @After
    public void after() {
        System.out.println("测试结束,执行时长: " + (System.currentTimeMillis() - timed) + "ms");
    }

    // 模拟并发请求的数量
    private static final int THREAD_NUM = 2000;

    private CountDownLatch cdl = new CountDownLatch(THREAD_NUM);

    /**
     * 性能测试
     */
    @Test
    public void benchmark() {
        // 创建线程，但并不是马上发起请求
        Thread[] threads = new Thread[THREAD_NUM];
        for (int i = 0; i < THREAD_NUM; i++) {
            Thread thread = new Thread(() -> {
                try {
                    cdl.await();
                    // xxService.doXx();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            threads[i] = thread;

            thread.start();

            cdl.countDown();
        }

        for (Thread t:threads){
            try {
                // 最后执行主线程
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}