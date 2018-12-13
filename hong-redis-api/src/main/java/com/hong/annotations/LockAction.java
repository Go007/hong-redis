package com.hong.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LockAction {

    /**
     * 锁的资源，key。支持spring El表达式
     */
    @AliasFor("key")
    String value() default "'default'";

    /**
     * 持锁时间,单位毫秒
     */
    long keepMills() default 3000;

    /**
     * 当获取失败时候动作,默认不重试
     */
    LockFailAction action() default LockFailAction.GIVEUP;

    enum LockFailAction {
        /**
         * 放弃
         */
        GIVEUP,
        /**
         * 继续
         */
        CONTINUE;
    }

    /**
     * 重试的间隔时间,设置GIVEUP忽略此项
     */
    long sleepMills() default 200;

    /**
     * 重试次数
     */
    int retryTimes() default 3;

    /**
     * 是否在方法执行完后手动触发释放锁
     * true:方法执行完后手动触发释放锁,即使此时锁还没有到设定的过期时间
     * false:自动释放锁,等待锁的过期时间到后自动释放
     * @return
     */
    boolean isManualReleaseLock() default true;
}
