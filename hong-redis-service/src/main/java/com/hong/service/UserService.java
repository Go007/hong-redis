package com.hong.service;

import com.hong.annotations.LockAction;
import com.hong.annotations.RedisCacheable;
import com.hong.annotations.RedisParameterLocked;
import com.hong.common.bean.Result;
import com.hong.entity.User;
import com.hong.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    private static Long count = 0L;

    @LockAction("lockKey")
    public void updateCount() {
        for (int i=0;i<100;i++){
            count++;
        }
        /**
         * 经测试发现:
         * 如果不加 @LockAction注解,狂按F5,数据库中count字段的值会出现无规律的增加;
         * 而加了@LockAction注解,狂按F5,值总会是100的倍数
         */
        userMapper.updateCount(1L,count,new Date());
    }

    @LockAction(value = "updateCount:",keepMills = 5000,isManualReleaseLock = false)
    public void submit(@RedisParameterLocked Long userId){
        /**
         * 模拟场景:限制同一个用户在5s内只能给自己的计数值+1
         */
        User user = userMapper.selectById(userId);
        Long count = user.getCount();
        count++;
        userMapper.updateCount(userId, count , new Date());
    }

    @LockAction(value="repayment:",keepMills = 60*1000,msg = "正在配账中，避免重复还款，请5-10分钟后再试",isManualReleaseLock = false)
    public Result submitRepaymentOrder(@RedisParameterLocked Integer idPerson) {
        Result result = new Result();

        return result;
    }

    /**
     * @Cacheable value属性是必须指定的,其表示当前方法的返回值是会被缓存在哪个Cache上
     * @Cacheable 不能指定特定的过期时间
     * @param userId
     * @return
     */
    // @Cacheable(value = "user")
    @RedisCacheable(value = "user",cacheTime = 5,timeUnit = TimeUnit.MINUTES,type = Result.class)
    public Result getUser(Long userId) {
        Result result = new Result();
        User user = userMapper.selectById(userId);
        result.setData(user);
        return result;
    }
}
