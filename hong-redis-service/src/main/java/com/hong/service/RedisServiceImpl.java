package com.hong.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService{

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @SuppressWarnings("unchecked")
    @Override
    public void del(String... key) {
        if (null != key && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
                stringRedisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
                stringRedisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    @Override
    public Integer getInt(String key) {
        String value = stringRedisTemplate.boundValueOps(key).get();
        if (StringUtils.isNotBlank(value)) {
            return Integer.valueOf(value);
        }
        return null;
    }

    @Override
    public String getStr(String key) {
        return stringRedisTemplate.boundValueOps(key).get();
    }

    @Override
    public String getAndSetStr(String key, String value) {
        return stringRedisTemplate.boundValueOps(key).getAndSet(value);
    }


    @Override
    public Boolean setStrNX(String key, String value) {

        return stringRedisTemplate.execute(new SessionCallback<Boolean>() {
            @Override
            public Boolean execute(RedisOperations redisOperations) throws DataAccessException {
                return redisOperations.boundValueOps(key).setIfAbsent(value);
            }
        });
    }

    @Override
    public String getStr(String key, boolean retain) {
        String value = stringRedisTemplate.boundValueOps(key).get();
        if (!retain) {
            redisTemplate.delete(key);
        }
        return value;
    }

    @Override
    public Object getObj(String key) {
        return redisTemplate.boundValueOps(key).get();
    }

    @Override
    public Object getObj(String key, boolean retain) {
        Object obj = redisTemplate.boundValueOps(key).get();
        if (!retain) {
            redisTemplate.delete(key);
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Class<T> clazz) {
        return (T) redisTemplate.boundValueOps(key).get();
    }

    @Override
    public <T> T getJson(String key, Class<T> clazz) {
        return JSONObject.parseObject(stringRedisTemplate.boundValueOps(key).get(), clazz);
    }

    @Override
    public void set(String key, Object value, long time) {
        boolean flag = true;
        if (value.getClass().equals(String.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Integer.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Double.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Float.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Short.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Long.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Boolean.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else {
            flag = false;
            redisTemplate.opsForValue().set(key, value);
        }
        if (time > 0) {
            if (flag) {
                stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
            } else {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void set(String key, Object value) {
        if (value.getClass().equals(String.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Integer.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Double.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Float.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Short.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Long.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Boolean.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else {
        redisTemplate.opsForValue().set(key, value);
        }
    }

    @Override
    public void setJson(String key, Object value, int time) {
        stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(value));
        if (time > 0) {
            stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
    }

    @Override
    public void setJson(String key, Object value) {
        stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(value));
    }

    @Override
    public void setJsonField(String key, String field, String value) {
        JSONObject obj = JSON.parseObject(stringRedisTemplate.boundValueOps(key).get());
        obj.put(field, value);
        stringRedisTemplate.opsForValue().set(key, obj.toJSONString());
    }

    @Override
    public Double decr(String key, double by) {
        return redisTemplate.opsForValue().increment(key, -by);
    }

    @Override
    public Double incr(String key, double by) {
        return redisTemplate.opsForValue().increment(key, by);
    }

    @Override
    public Double getDouble(String key) {
        String value = stringRedisTemplate.boundValueOps(key).get();
        if (StringUtils.isNoneBlank(value)) {
            return Double.valueOf(value);
        }
        return 0d;
    }

    @Override
    public void setDouble(String key, double value, int time) {
        stringRedisTemplate.boundValueOps(key).get();
        if (time > 0) {
            stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
    }

    @Override
    public void setDouble(String key, double value) {
        stringRedisTemplate.boundValueOps(key).get();
    }

    @Override
    public void setInt(String key, int value, int time) {
        stringRedisTemplate.opsForValue().set(key, String.valueOf(value));
        if (time > 0) {
            stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
    }

    @Override
    public void setInt(String key, int value) {
        stringRedisTemplate.opsForValue().set(key, String.valueOf(value));
    }

    @Override
    public <T> void setMap(String key, Map<String, T> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public void setMap(String key, Map<String, Object> map, int time) {
        redisTemplate.opsForHash().putAll(key, map);
        if (time > 0) {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
    }

    @Override
    public <T> void setMap(String key, T obj) {
        Map<String, String> map = (Map<String, String>)JSON.parseObject(JSON.toJSONString(obj), Map.class);
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public <T> void addMap(String key, Map<String, T> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public void addMap(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    @Override
    public <T> void addMap(String key, String field, T obj) {
        redisTemplate.opsForHash().put(key, field, obj);
    }

    @Override
    public <T> Map<String, T> mget(String key, Class<T> clazz) {
        BoundHashOperations<String, String, T> boundHashOps = redisTemplate.boundHashOps(key);
        return boundHashOps.entries();
    }

    @Override
    public Map<String, Object> getMap(String key) {
        BoundHashOperations<String, String, Object> boundHashOps = redisTemplate.boundHashOps(key);
        return boundHashOps.entries();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getMapField(String key, String field, Class<T> clazz) {
        return (T) redisTemplate.boundHashOps(key).get(field);
    }

    @Override
    public <T>  List<T> getMapLon(String key, String field, Class clazz) {

        return redisTemplate.execute(new SessionCallback<List<T>>() {
            @Override
            public List<T> execute(RedisOperations redisOperations) throws DataAccessException {


                return redisOperations.boundHashOps(key).multiGet(Arrays.asList(new String[]{field}));
            }
        });
    }

    @Override
    public void delMapField(String key, String... field) {
        BoundHashOperations<String, String, ?> boundHashOps = redisTemplate.boundHashOps(key);
        boundHashOps.delete(field);
    }

    @Override
    public void expire(String key, int time) {
        if (time > 0) {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
    }

    @Override
    public void expireSecond(String key, long time) {
        if (time > 0) {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
    }

    @Override
    public void expireMsec(String key, long time) {
        if (time > 0) {
            redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void strSadd(String key, String... value) {
        this.stringRedisTemplate.boundSetOps(key).add(value);
    }

    @Override
    public Set<String> smembers(String key) {
        return this.stringRedisTemplate.boundSetOps(key).members();
    }

    @Override
    public void sadd(String key, String... value) {
        redisTemplate.boundSetOps(key).add(value);
    }

    @Override
    public void srem(String key, String... value) {
        redisTemplate.boundSetOps(key).remove(value);
    }

    @Override
    public void srename(String oldkey, String newkey) {
        redisTemplate.boundSetOps(oldkey).rename(newkey);
    }

    @Override
    public Boolean sisMember(String key, String value) {
        return this.stringRedisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    @Override
    public Boolean zAdd(String key, double score, String value) {

        Boolean isSuccess = stringRedisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) connection;
                Boolean set = stringRedisConnection.zAdd(key, score, value);
                return set;
            }
        });
        return isSuccess == null ? false : isSuccess;
    }

    @Override
    public Set<String> zRange(String key, long start, long end) {

        Set<String> setStr = stringRedisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) connection;
                Set<String> set = stringRedisConnection.zRange(key, start, end);
                return set;
            }
        });
        return setStr;
    }

    @Override
    public Long zCard(String key) {

        Long count = stringRedisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) connection;
                Long count = stringRedisConnection.zCard(key);
                return count;
            }
        });
        return count;
    }

    @Override
    public Set<String> zRevRange(String key, long start, long end) {
        Set<String> setStr = stringRedisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) connection;
                Set<String> set = stringRedisConnection.zRevRange(key, start, end);
                return set;
            }
        });
        return setStr;
    }

    @Override
    public Set<String> zRangeByScore(String key, double min, double max) {
        Set<String> setStr = stringRedisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) connection;
                Set<String> set = stringRedisConnection.zRangeByScore(key, min, max);
                return set;
            }
        });
        return setStr;
    }

    @Override
    public Set<String> zRevRangeByScore(String key, double min, double max) {
        Set<String> setStr = stringRedisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) connection;
                Set<String> set = stringRedisConnection.zRevRangeByScore(key, min, max);
                return set;
            }
        });
        return setStr;
    }

    @Override
    public List<Long> hMGetLon(String key, List<String> fields) {

        return redisTemplate.execute(new SessionCallback<List<Long>>() {
            @Override
            public List<Long> execute(RedisOperations redisOperations) throws DataAccessException {
                return redisOperations.boundHashOps(key).multiGet(fields);
            }
        });
    }

    @Override
    public List hMGet(String key, List fields) {
        return redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations redisOperations) throws DataAccessException {
                return redisOperations.boundHashOps(key).multiGet(fields);
            }
        });
    }

    @Override
    public void hMSet(String key, Map map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
}
