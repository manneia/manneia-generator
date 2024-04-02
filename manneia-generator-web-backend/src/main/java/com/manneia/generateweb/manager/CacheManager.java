package com.manneia.generateweb.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存操作类
 *
 * @author lkx
 */
@Component
public class CacheManager {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 本地缓存
     */
    Cache<String, Object> localCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    /**
     * 写入缓存
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, Object value) {
        localCache.put(key, value);
        redisTemplate.opsForValue().set(key, value, 100, TimeUnit.MINUTES);
    }

    /**
     * 读取缓存
     *
     * @param key 键
     * @return 返回缓存值
     */
    public Object get(String key) {
        // 先从本地缓存中尝试读取
        Object value = localCache.getIfPresent(key);
        if (value != null) {
            return value;
        }
        // 本地缓存未命中,尝试从redis中获取
        value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            // 缓存命中,写入本地缓存
            localCache.put(key, value);
        }
        return value;
    }

    /**
     * 移除缓存
     *
     * @param key 键
     */
    public void delete(String key) {
        localCache.invalidate(key);
        redisTemplate.delete(key);
    }
}