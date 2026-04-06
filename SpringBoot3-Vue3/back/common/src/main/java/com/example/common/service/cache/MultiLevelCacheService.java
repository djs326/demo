package com.example.common.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Service
public class MultiLevelCacheService {

    @Autowired
    private CacheManager caffeineCacheManager;

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Autowired
    private CacheProtectionService cacheProtectionService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public <T> T get(String cacheName, String key, Callable<T> valueLoader) {
        if (cacheProtectionService.isPossibleCachePenetration(key)) {
            return null;
        }

        if (!cacheProtectionService.tryAcquireToken()) {
            throw new RuntimeException("Too many requests, please try again later");
        }

        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            Cache.ValueWrapper valueWrapper = caffeineCache.get(key);
            if (valueWrapper != null) {
                return (T) valueWrapper.get();
            }
        }

        org.springframework.data.redis.cache.RedisCache redisCache = (org.springframework.data.redis.cache.RedisCache) redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            Cache.ValueWrapper valueWrapper = redisCache.get(key);
            if (valueWrapper != null) {
                T value = (T) valueWrapper.get();
                if (caffeineCache != null) {
                    caffeineCache.put(key, value);
                }
                return value;
            }
        }

        if (!cacheProtectionService.tryAcquireSemaphore(key)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return get(cacheName, key, valueLoader);
        }

        try {
            T value = valueLoader.call();
            if (value != null) {
                cacheProtectionService.markKeyExists(key);
                if (caffeineCache != null) {
                    caffeineCache.put(key, value);
                }
                if (redisCache != null) {
                    redisCache.put(key, value);
                }
            } else {
                cacheProtectionService.addNullValueProtection(cacheName, key, 5, TimeUnit.MINUTES);
            }
            return value;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load cache value", e);
        } finally {
            cacheProtectionService.releaseSemaphore(key);
        }
    }

    public <T> T getFromLocal(String cacheName, String key) {
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            Cache.ValueWrapper valueWrapper = caffeineCache.get(key);
            if (valueWrapper != null) {
                return (T) valueWrapper.get();
            }
        }
        return null;
    }

    public <T> T getFromRedis(String cacheName, String key) {
        org.springframework.data.redis.cache.RedisCache redisCache = (org.springframework.data.redis.cache.RedisCache) redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            Cache.ValueWrapper valueWrapper = redisCache.get(key);
            if (valueWrapper != null) {
                return (T) valueWrapper.get();
            }
        }
        return null;
    }

    public void put(String cacheName, String key, Object value) {
        cacheProtectionService.markKeyExists(key);
        
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            caffeineCache.put(key, value);
        }

        org.springframework.data.redis.cache.RedisCache redisCache = (org.springframework.data.redis.cache.RedisCache) redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            redisCache.put(key, value);
        }
    }

    public void put(String cacheName, String key, Object value, long timeout, TimeUnit unit) {
        cacheProtectionService.markKeyExists(key);
        
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            caffeineCache.put(key, value);
        }

        String redisKey = cacheName + ":" + key;
        redisTemplate.opsForValue().set(redisKey, value, timeout, unit);
    }

    public void putIfAbsent(String cacheName, String key, Object value) {
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            caffeineCache.putIfAbsent(key, value);
        }

        org.springframework.data.redis.cache.RedisCache redisCache = (org.springframework.data.redis.cache.RedisCache) redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            redisCache.putIfAbsent(key, value);
        }
    }

    public void putAll(String cacheName, Map<? extends Object, ? extends Object> map) {
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            for (Map.Entry<? extends Object, ? extends Object> entry : map.entrySet()) {
                caffeineCache.put(entry.getKey(), entry.getValue());
                cacheProtectionService.markKeyExists(String.valueOf(entry.getKey()));
            }
        }

        org.springframework.data.redis.cache.RedisCache redisCache = (org.springframework.data.redis.cache.RedisCache) redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            for (Map.Entry<? extends Object, ? extends Object> entry : map.entrySet()) {
                redisCache.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public boolean exists(String cacheName, String key) {
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            Cache.ValueWrapper valueWrapper = caffeineCache.get(key);
            if (valueWrapper != null) {
                return true;
            }
        }

        org.springframework.data.redis.cache.RedisCache redisCache = (org.springframework.data.redis.cache.RedisCache) redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            Cache.ValueWrapper valueWrapper = redisCache.get(key);
            return valueWrapper != null;
        }
        return false;
    }

    public void evict(String cacheName, String key) {
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            caffeineCache.evict(key);
        }

        org.springframework.data.redis.cache.RedisCache redisCache = (org.springframework.data.redis.cache.RedisCache) redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            redisCache.evict(key);
        }
    }

    public void evictAll(String cacheName, Collection<String> keys) {
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            for (String key : keys) {
                caffeineCache.evict(key);
            }
        }

        org.springframework.data.redis.cache.RedisCache redisCache = (org.springframework.data.redis.cache.RedisCache) redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            for (String key : keys) {
                redisCache.evict(key);
            }
        }
    }

    public void clear(String cacheName) {
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            caffeineCache.clear();
        }

        org.springframework.data.redis.cache.RedisCache redisCache = (org.springframework.data.redis.cache.RedisCache) redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            redisCache.clear();
        }
    }

    public boolean expire(String cacheName, String key, long timeout, TimeUnit unit) {
        String redisKey = cacheName + ":" + key;
        return Boolean.TRUE.equals(redisTemplate.expire(redisKey, timeout, unit));
    }

    public long getExpire(String cacheName, String key) {
        String redisKey = cacheName + ":" + key;
        Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        return ttl != null ? ttl : -1;
    }

    public void refreshLocalCache(String cacheName, String key) {
        org.springframework.data.redis.cache.RedisCache redisCache = (org.springframework.data.redis.cache.RedisCache) redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            Cache.ValueWrapper valueWrapper = redisCache.get(key);
            if (valueWrapper != null) {
                Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
                if (caffeineCache != null) {
                    caffeineCache.put(key, valueWrapper.get());
                }
            }
        }
    }
}
