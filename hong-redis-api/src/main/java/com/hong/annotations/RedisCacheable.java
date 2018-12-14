package com.hong.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * <br>Redis Cache注解</br>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheable {

    String value() default "";

    long cacheTime() default -1;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /* 数据返回类型 */
    Class type() default String.class;
}
