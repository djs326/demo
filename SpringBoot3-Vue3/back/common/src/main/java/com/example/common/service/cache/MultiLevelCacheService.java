package com.example.common.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

@Service
public class MultiLevelCacheService {

    @Autowired
    private CacheManager caffeineCacheManager;

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Autowired
    private CacheProtectionService cacheProtectionService;

    public <T> T get(String cacheName, String key, Callable<T> valueLoader) {
        // 1. 检查是否可能是缓存穿透
        if (cacheProtectionService.isPossibleCachePenetration(key)) {
            return null;
        }

        // 2. 尝试获取令牌，进行限流
        if (!cacheProtectionService.tryAcquireToken()) {
            throw new RuntimeException("Too many requests, please try again later");
        }

        // 3. 先从本地缓存获取
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            Cache.ValueWrapper valueWrapper = caffeineCache.get(key);
            if (valueWrapper != null) {
                return (T) valueWrapper.get();
            }
        }

        // 4. 从Redis缓存获取
        org.springframework.data.redis.cache.RedisCache redisCache = (org.springframework.data.redis.cache.RedisCache) redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            Cache.ValueWrapper valueWrapper = redisCache.get(key);
            if (valueWrapper != null) {
                T value = (T) valueWrapper.get();
                // 将Redis缓存的值同步到本地缓存
                if (caffeineCache != null) {
                    caffeineCache.put(key, value);
                }
                return value;
            }
        }

        // 5. 如果都没有，尝试获取信号量，防止缓存击穿
        if (!cacheProtectionService.tryAcquireSemaphore(key)) {
            // 如果获取不到信号量，等待一段时间后重试
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // 重试获取缓存
            return get(cacheName, key, valueLoader);
        }

        // 6. 调用valueLoader加载数据
        try {
            T value = valueLoader.call();
            if (value != null) {
                // 标记key存在，防止缓存穿透
                cacheProtectionService.markKeyExists(key);
                // 同时更新本地缓存和Redis缓存
                if (caffeineCache != null) {
                    caffeineCache.put(key, value);
                }
                if (redisCache != null) {
                    redisCache.put(key, value);
                }
            }
            return value;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load cache value", e);
        } finally {
            // 释放信号量
            cacheProtectionService.releaseSemaphore(key);
        }
    }

    public void put(String cacheName, String key, Object value) {
        // 标记key存在，防止缓存穿透
        cacheProtectionService.markKeyExists(key);
        
        // 同时更新本地缓存和Redis缓存
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            caffeineCache.put(key, value);
        }

        org.springframework.data.redis.cache.RedisCache redisCache = (org.springframework.data.redis.cache.RedisCache) redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            redisCache.put(key, value);
        }
    }

    public void evict(String cacheName, String key) {
        // 同时删除本地缓存和Redis缓存
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            caffeineCache.evict(key);
        }

        org.springframework.data.redis.cache.RedisCache redisCache = (org.springframework.data.redis.cache.RedisCache) redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            redisCache.evict(key);
        }
    }

    public void clear(String cacheName) {
        // 同时清空本地缓存和Redis缓存
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            caffeineCache.clear();
        }

        org.springframework.data.redis.cache.RedisCache redisCache = (org.springframework.data.redis.cache.RedisCache) redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            redisCache.clear();
        }
    }
}
