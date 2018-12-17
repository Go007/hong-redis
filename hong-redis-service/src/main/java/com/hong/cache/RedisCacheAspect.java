package com.hong.cache;

import com.alibaba.fastjson.JSON;
import com.hong.annotations.RedisCacheable;
import com.hong.service.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Aspect
@Order(3)
@Component
public class RedisCacheAspect {

    protected Logger logger = LoggerFactory.getLogger(RedisCacheAspect.class);

    @Autowired
    private RedisService redisService;

    @Autowired
    private KeyGenerator keyGenerator;

    /**
     * 方法调用前，先查询缓存。如果存在缓存，则返回缓存数据，阻止方法调用; 如果没有缓存，则调用业务方法，然后将结果放到缓存中
     *
     * @param pjp
     * @param redisCache
     * @return
     * @throws Throwable
     */
    @Around("@annotation(redisCache)")
    public Object cache(ProceedingJoinPoint pjp, RedisCacheable redisCache) throws Throwable {

        // 获取方法对象Method
        /**
         * 不能使用下面的这段方法,这种方式获取到的方法是接口的方法而不是具体的实现类的方法，因此是错误的。
         * Signature signature = pjp.getSignature();
         * MethodSignature methodSignature = (MethodSignature)signature;
         * Method targetMethod = methodSignature.getMethod();
         */
        Object target = pjp.getTarget();
        String clazzName = target.getClass().getName();
        Signature sig = pjp.getSignature();
        String methodName = pjp.getSignature().getName();
        MethodSignature msig = (MethodSignature) sig;
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        Object[] args = pjp.getArgs();
        String cacheKey = redisCache.value();
        if (StringUtils.isEmpty(cacheKey)) {
            cacheKey = keyGenerator.generate(target, currentMethod, args).toString();
        }
        // 根据类名，方法名和参数生成key
        logger.info("根据类名[{}]，方法名[{}]和参数[{}]，生成key[{}]", clazzName, methodName, args, cacheKey);
        long cacheTime = redisCache.cacheTime();
        TimeUnit timeUnit = redisCache.timeUnit();
        cacheTime = timeUnit.toMillis(cacheTime);

        Object result = null;
        String redisResult = null;
        if (!redisService.exists(cacheKey)) {
            logger.info("缓存未命中,key为[{}]", cacheKey);
            result = pjp.proceed(args);
            if (Objects.nonNull(result)) {
                // 序列化查询结果
                redisResult = JSON.toJSONString(result);
                // 序列化结果放入缓存
            }
            /**
             * 缓存穿透：查询数据不存在DB中从而cache中也不存在，查询操作频繁落入DB中而导致DB压力过大；为了避免此问题有如下解决方案：
             * ①  存null值：查询DB为空时，依然存 key – null √
             * ②  布隆过滤：维护一张存在缓存key的bitmap表，查询时进行filter过滤
             */
            redisService.set(cacheKey, redisResult, cacheTime);
        } else {
            Class modelType = redisCache.type();
            redisResult = redisService.getStr(cacheKey);
            // 反序列化从缓存中拿到的json
            Class clazz = ((MethodSignature) pjp.getSignature()).getReturnType();
            // 序列化结果应该是List对象
            if (clazz.isAssignableFrom(List.class)) {
                result = JSON.parseArray(redisResult, modelType);
            } else {
                // 序列化结果是普通对象
                result = JSON.parseObject(redisResult, clazz);
            }
            logger.info("缓存命中，key为[{}]，result为[{}]", cacheKey, result);
        }
        return result;
    }

}
