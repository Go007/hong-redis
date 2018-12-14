package com.hong.controller;

import com.hong.common.bean.Result;
import com.hong.entity.User;
import com.hong.lock.DistributedLock;
import com.hong.lock.RedissonLocker;
import com.hong.service.RedisService;
import com.hong.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("redis")
public class RedisController {

    @Resource
    private RedisTemplate<String, User> redisTemplate;

    @Autowired
    private UserService userService;

    @Resource
    private DistributedLock redisDistributedLock;

    private static Long count = 0L;

    @Resource
    private RedissonLocker redissonLocker;

    @Autowired
    private RedisService redisService;

    @RequestMapping("user/{id}")
    public Result save(@PathVariable long id) {
        User user = new User();
        user.setId(id);
        user.setName("测试");
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

                            /*redisDistributedLock.lock("lockKey", 20, 0, 0);
                            count++;*/

                            //userService.updateCount();

                            // 测试Redisson分布式锁
                            redissonLocker.lock("lockKey",() -> {
                                count++;
                                return null;
                            },30);

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

        result.setData(count);
        return result;
    }

    @GetMapping("/testLock/{userId}")
    public Result testLock(@PathVariable Long userId) {
        userService.submit(userId);
        return Result.buildSuccess();
    }

    /**
     * 模拟还款业务需求:还款频率限制,不能频繁还款,用户1min内只能还一次
     */
    @RequestMapping(value = "/repayment/notifyRepaymentSucceed", method = RequestMethod.POST)
    public Result notifyRepaymentSucceed(@RequestBody Map<String,Object> paramMap){
        // 1.校验还款订单状态
        // 2.生成订单标识，限制只能1min(配置文件中；默认配置静态变量中；)后再次生成订单；
        redisService.set(paramMap.get("idPerson") + ":repayment",paramMap.get("orderNo"), 60);
        // 3.配账
        return Result.buildSuccess();
    }

    @RequestMapping(value = "/repayment/createRepaymentOrder", method = RequestMethod.POST)
    public Result createRepaymentOrder(@RequestBody Map<String,Object> paramMap){
        Result result = new Result();
        String orderNo = redisService.getStr(paramMap.get("idPerson") + ":repayment");
        if (StringUtils.isNotEmpty(orderNo)){
            result.setMessage("该合同正在配账中，避免重复还款，请5-10分钟后再试");
        }
        return result;
    }

    /**
     *  使用Redis分布式锁解决,本质是限制用户在1min内不能重复提交订单
     */
    @RequestMapping(value = "/repayment/createRepaymentOrder2", method = RequestMethod.POST)
    public Result createRepaymentOrder2(@RequestBody Map<String,Object> paramMap){
        return userService.submitRepaymentOrder((Integer) paramMap.get("idPerson"));
    }

    /**
     * 测试Spring-cache
     */
    @GetMapping("/getUser/{userId}")
    public Result getUser(@PathVariable Long userId){
        return userService.getUser(userId);
    }
}
