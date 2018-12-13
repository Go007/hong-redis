package com.hong.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * redis操作接口
 *
 */
public interface RedisService {

    /**
     * 删除缓存
     */
     void del(String... key);

    /**
     * 取得缓存(int)
     */
     Integer getInt(String key);

    /**
     * 取得缓存(string)
     */
     String getStr(String key);


    /**
     * 获取旧值设置新值(String)
     */
    String getAndSetStr(String key, String value);

    /**
     * 不存在设置值，存在不设置(String)
     */
    Boolean setStrNX(String key, String value);

    /**
     * 取得缓存(string)
     *
     * @param retain 是否保留缓存
     */
     String getStr(String key, boolean retain);

    /**
     * 取得缓存 基本数据类型(Character除外), 请直接使用get(String key, Class<T> clazz)取值
     */
     Object getObj(String key);

    /**
     * 取得缓存 java 8种基本类型的数据请直接使用get(String key, Class<T> clazz)取值
     *
     * @param retain 是否保留缓存
     */
     Object getObj(String key, boolean retain);

    /**
     * 获取缓存(获取基本类型请使用此方法获取)
     */
     <T> T get(String key, Class<T> clazz);

    /**
     * 获取缓存json对象
     */
     <T> T getJson(String key, Class<T> clazz);

    /**
     * 将value对象写入缓存
     *
     * @param time 默认为秒
     */
     void set(String key, Object value, long time);

    /**
     * 将value对象写入缓存(默认时间)
     */
     void set(String key, Object value);

    /**
     * 将value对象以JSON格式写入缓存
     *
     * @param time 默认为秒
     */
     void setJson(String key, Object value, int time);

    /**
     * 将value对象以JSON格式写入缓存 (默认时间)
     */
     void setJson(String key, Object value);

    /**
     * 更新key对象field的值
     *
     * @param key 缓存key
     * @param field 缓存对象field
     * @param value 缓存对象field值
     */
     void setJsonField(String key, String field, String value);

    /**
     * 递减操作
     */
     Double decr(String key, double by);

    /**
     * 递增操作
     */
     Double incr(String key, double by);

    /**
     * 获取double类型值
     */
     Double getDouble(String key);

    /**
     * 设置double类型值
     *
     * @param time 秒
     */
     void setDouble(String key, double value, int time);

    /**
     * 设置double类型值
     */
     void setDouble(String key, double value);

    /**
     * 设置int类型值
     */
     void setInt(String key, int value, int time);

    /**
     * 设置int类型值
     */
     void setInt(String key, int value);

    /**
     * 将map写入缓存
     */
     <T> void setMap(String key, Map<String, T> map);


    /**
     * 将map写入缓存
     */
     void setMap(String key, Map<String, Object> map, int time);

    /**
     * 将map写入缓存
     */
     <T> void setMap(String key, T obj);

    /**
     * 向key对应的map中添加缓存对象
     */
     <T> void addMap(String key, Map<String, T> map);

    /**
     * 向key对应的map中添加缓存对象
     */
     void addMap(String key, String field, String value);

    /**
     * 向key对应的map中添加缓存对象
     */
     <T> void addMap(String key, String field, T obj);

    /**
     * 获取map缓存
     */
     <T> Map<String, T> mget(String key, Class<T> clazz);

    /**
     * 获取map缓存
     */
     Map<String, Object> getMap(String key);


    /**
     * 获取map缓存中的某个对象
     */
     <T> T getMapField(String key, String field, Class<T> clazz);


    <T>  List<T> getMapLon(String key, String field, Class clazz);

    /**
     * 删除map中的某个对象
     */
     void delMapField(String key, String... field);

    /**
     * 指定缓存的失效时间
     *
     * @param key 缓存KEY
     * @param time 失效时间(秒)
     */
     void expire(String key, int time);

    /**
     * <br>指定秒数key的过期时间</br>
     *
     * @version   1.0
     * @since     1.0
     * @param key 键
     */
    void expireSecond(String key, long time);

    /**
     * <br>指定毫秒数key的过期时间</br>
     *
     * @version   1.0
     * @since     1.0
     * @param key 键
     */
    void expireMsec(String key, long time);

    /**
     * 添加set
     */
     void strSadd(String key, String... value);

    /**
     * 获取set
     * @param key
     * @return
     */
     Set<String> smembers(String key);

    /**
     * 添加set
     */
     void sadd(String key, String... value);

    /**
     * 删除set集合中的对象
     */
     void srem(String key, String... value);

    /**
     * set重命名
     */
     void srename(String oldkey, String newkey);

    /**
     * set: Check if set at key contains value.
     * @param key
     * @param value
     * @return
     */
    Boolean sisMember(String key, String value);

    /**
     * 模糊查询keys
     */
     Set<String> keys(String pattern);


    /**
     * <br>ZSet集合:通过key-score写入Redis</br>
     *
     * @version   1.0
     * @since     1.0
     * @param key 键
     * @param score 分数
     * @param value 值
     * @return Boolean
     */
     Boolean zAdd(String key, double score, String value);

    /**
     *<br>  ZSet集合分页：通过元素index来获取集合区间的元素并做Asc排序操作<br/>
     * @version   1.0
     * @since     1.0
     * @param key 键
     * @param start 开始元素
     * @param end 结束元素
     * @return Set<String>
     * @exception
     */
     Set<String> zRange(String key, long start, long end);

    /**
     * <br>查询指定key的集合元素数量</br>
     * @version   1.0
     * @since     1.0
     * @param     key 键
     * @return
     * @exception
     */
     Long zCard(String key);


    /**
     *<br> ZSet集合分页：通过元素index来获取集合区间的元素并做Desc排序操作<br/>
     *
     * @version   1.0
     * @since     1.0
     * @param key 键
     * @param start 开始元素
     * @param end 结束元素
     * @return Set<String>
     * @exception
     */
     Set<String> zRevRange(String key, long start, long end);


    /**
     * <br>ZSet集合：通过积分来获取集合区间的元素并做Asc排序操作</br>
     *
     * @version   1.0
     * @since     1.0
     * @param key 键
     * @param min 积分上限
     * @param max 积分下限
     * @return Set<String>
     * @exception
     */
     Set<String> zRangeByScore(String key, double min, double max);

    /**
     * <br>ZSet集合：通过积分来获取集合区间的元素并做Desc排序操作</br>
     * 注：返回集合为HashSet集合
     * @version   1.0
     * @since     1.0
     * @param key 键
     * @param min 积分上限
     * @param max 积分下限
     * @return Set<String>
     * @exception
     */
     Set<String> zRevRangeByScore(String key, double min, double max);

    /**
     * <br>Hash集合：通过fields批量获取map的值</br>
     *
     * @param key 键
     * @param fields map keys
     * @return List<Long>
     * @version 1.0
     * @since 1.0
     */
    List<Long> hMGetLon(String key, List<String> fields);

    /**
     * <br>Hash集合：通过fields批量获取map的值</br>
     *
     * @param key 键
     * @param fields map keys
     * @return List<Long>
     * @version 1.0
     * @since 1.0
     */
    List hMGet(String key, List fields);

    /**
     * <br>Hash集合：通过map集合批量写入Redis</br>
     *
     * @param key 键
     * @param map map值
     * @version 1.0
     * @since 1.0
     */
    void hMSet(String key, Map map);

}
