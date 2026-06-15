package com.realworld.blog.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;
/**
 * @author jiaolei
 * @date 2026/6/5 16:18
 * @description
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存（带过期时间，单位：秒）
     */
    public void set(String key, Object value, long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 设置缓存（永久）
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取缓存
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 设置过期时间
     */
    public boolean expire(String key, long seconds) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, seconds, TimeUnit.SECONDS));
    }
}