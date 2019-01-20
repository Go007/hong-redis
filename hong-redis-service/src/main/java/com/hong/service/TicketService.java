package com.hong.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by John on 2019/1/20.
 * 模拟12306查询剩余票数
 */
@Service
public class TicketService {

    private final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private RedisService redisService;

    /**
     * k/V 存储查询不同车次的锁的状态，主要是为了防止使用Lock锁时的锁的细粒度控制
     */
    private ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>();

    private static final Object lock = new Object();

    public Object queryTicketStock(final String ticketSeq) {
        String value = redisService.getStr(ticketSeq);
        if (value != null) {
            logger.info(Thread.currentThread().getName() + "从缓存中取得数据:" + value);
            return value;
        }

        boolean lock = false;
        try {
            // 类似redis的setnx操作
            lock = lockMap.putIfAbsent(ticketSeq, lock) == null;
            if (lock) {
                //缓存重建
                //value = databaseService.queryFromDb(ticketSeq); //模拟从数据库中查询
                value = "100";

                //塞到缓存
                redisService.set(ticketSeq, value, 5 * 60 * 1000);

                //双写，写入备份缓存
                // bakRedisService.set();

            } else { //没拿到锁怎么办？
                /**
                 * 服务降级
                 * 缓存降级：根据业务场景，选择合适的降级
                 * 1.重试，争抢锁，
                 * 2.直接返回一个空
                 * 3.备份缓存
                 */
                //value = bakRedisService.get(ticketSeq);
                if (value != null) {
                    logger.info(Thread.currentThread().getName() + "缓存降级，从备份从缓存中取得数据:" + value);
                } else {
                    value = "0";
                }
            }
        } finally {
            if (lock) {
                lockMap.remove(ticketSeq);
            }
        }
        return value;
    }

}
