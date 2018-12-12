package com.hong.annotations;

import java.lang.annotation.*;

/**
 * Redis分布式锁细粒度控制注解（用于方法参数上）
 * 应用示例：
 * redis锁，限定用户10秒内只能发一个贴, 避免重复提交
 * @RedisLock(key = "faq-submit-question-1", expireTime = 10000, msg = "操作过于频繁")
 * public String submit(@RedisParameterLocked String userId, QuestionParam param) {}
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RedisParameterLocked {

}
