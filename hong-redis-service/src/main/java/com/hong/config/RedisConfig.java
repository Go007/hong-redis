package com.hong.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <br>Sprig Boot Redis配置类</br>
 *  继承CachingConfigurerSupport并重写方法，配合@EnableCaching注解实现spring缓存框架的使用
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * redis key 分隔符
     */
    public static final Object DELIMITER = "-";

    public static final String NO_PARAM_KEY = "NO_PARAM";
    public static final String NULL_PARAM_KEY = "NULL_PARAM";

    //全局的默认缓存时间 5min
    public static final long DEFALUT_CACHE_TIME = 5*60*1000;

    @SuppressWarnings("rawtypes")
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        /**
         * spring-cache可以搭配的缓存很多,也就有很多org.springframework.cache.CacheManager使用
         * 这里用的是Redis,所以是RedisCacheManager
         */
        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
        /**
         *  设置默认的缓存过期时间
         *  因为 @Cacheable 注解本身不支持配置过期时间,这里设置一个全局的过期时间
         */
        rcm.setDefaultExpiration(DEFALUT_CACHE_TIME);//秒
        // 也可以在Map中给不同的key设置对应的过期时间
        /*Map<String,Long> keyExpireMap = new HashMap<>();
        keyExpireMap.put("key",1000L);
        rcm.setExpires(keyExpireMap);*/
        return rcm;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * key 有两种生成方式，第一种是直接指定，这种方式一般用于某些定制化的缓存当中，
     * 通用返回接口的缓存得要用第二种方式，也就是通过键生成器来生成缓存的 key。
     * Spring Cache中提供了默认的 Key 生成器 org.springframework.cache.interceptor.SimpleKeyGenerator
     * 来生成 key，但是这个 key 是不会将函数名组合在 key 中，也是有缺陷，所以我们需要自定义一个 keyGenerator
     * @return
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder key = new StringBuilder();
                //先将类的全限定名和方法名拼装在 key 中
                key.append(method.getDeclaringClass().getName()).append(".").append(method.getName()).append(":");
                if (params.length == 0) {
                    return key.append(NO_PARAM_KEY).toString();
                }
                //通过遍历参数,将参数也拼装在 key 中,保证每次获取key 的唯一性
                for (Object param : params) {
                    if (param == null) {
                        key.append(NULL_PARAM_KEY);
                    } else if (ClassUtils.isPrimitiveArray(param.getClass())) {
                        int length = Array.getLength(param);
                        for (int i = 0; i < length; i++) {
                            key.append(Array.get(param, i));
                            key.append(',');
                        }
                    } else if (ClassUtils.isPrimitiveOrWrapper(param.getClass()) || param instanceof String) {
                        key.append(param);
                    } else {
                        key.append(param.hashCode());   //如果是map 或 model 类型
                    }
                    key.append(DELIMITER);
                }
                return  key.deleteCharAt(key.length() - 1).toString();
            }
        };
    }

}