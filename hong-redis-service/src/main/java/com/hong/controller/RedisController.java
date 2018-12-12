package com.hong.controller;

import com.hong.common.bean.Result;
import com.hong.entity.User;
import com.hong.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("redis")
public class RedisController {

    @Resource
    private RedisTemplate<String, User> redisTemplate;

    @Autowired
    private MyService myService;

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
        result.setData(myService.testCurrency());
        return result;
    }

}
