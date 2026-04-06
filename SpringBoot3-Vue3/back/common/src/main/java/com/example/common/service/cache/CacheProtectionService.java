package com.example.common.service.cache;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Service
public class CacheProtectionService {

    // 用于缓存穿透防护的布隆过滤器（简化实现，实际项目中可使用更高效的布隆过滤器）
    private final Map<String, Boolean> bloomFilter = new ConcurrentHashMap<>();

    // 用于缓存击穿防护的信号量
    private final Map<String, Semaphore> semaphoreMap = new ConcurrentHashMap<>();

    // 用于限流的令牌桶
    private final RateLimiter rateLimiter = RateLimiter.create(100);

    /**
     * 检查是否可能是缓存穿透
     */
    public boolean isPossibleCachePenetration(String key) {
        return !bloomFilter.containsKey(key);
    }

    /**
     * 标记key已经存在，防止缓存穿透
     */
    public void markKeyExists(String key) {
        bloomFilter.put(key, true);
    }

    /**
     * 尝试获取信号量，防止缓存击穿
     */
    public boolean tryAcquireSemaphore(String key) {
        Semaphore semaphore = semaphoreMap.computeIfAbsent(key, k -> new Semaphore(1));
        return semaphore.tryAcquire();
    }

    /**
     * 释放信号量
     */
    public void releaseSemaphore(String key) {
        Semaphore semaphore = semaphoreMap.get(key);
        if (semaphore != null) {
            semaphore.release();
        }
    }

    /**
     * 尝试获取令牌，进行限流
     */
    public boolean tryAcquireToken() {
        return rateLimiter.tryAcquire(1, TimeUnit.SECONDS);
    }

    /**
     * 为缓存键添加随机过期时间，防止缓存雪崩
     */
    public long getRandomExpireTime(long baseExpireTime) {
        return baseExpireTime + (long) (Math.random() * 10 * 60 * 1000); // 添加0-10分钟的随机时间
    }
}
