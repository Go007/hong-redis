package com.hong.service;

import com.hong.annotations.LockAction;
import com.hong.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        userMapper.updateCount(1L,count);
    }


}
