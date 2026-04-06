package com.example.common.service.cache;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CacheProtectionService {

    @Value("${cache.bloom-filter.expected-insertions:1000000}")
    private long expectedInsertions;

    @Value("${cache.bloom-filter.fpp:0.001}")
    private double fpp;

    @Value("${cache.rate-limiter.permits-per-second:100}")
    private double permitsPerSecond;

    @Value("${cache.random-expire-time-range:600000}")
    private long randomExpireTimeRange;

    private BloomFilter<String> bloomFilter;

    private final Map<String, Semaphore> semaphoreMap = new ConcurrentHashMap<>();

    private final Map<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    private RateLimiter rateLimiter;

    @PostConstruct
    public void init() {
        bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                expectedInsertions,
                fpp
        );
        rateLimiter = RateLimiter.create(permitsPerSecond);
    }

    public boolean isPossibleCachePenetration(String key) {
        return !bloomFilter.mightContain(key);
    }

    public void markKeyExists(String key) {
        bloomFilter.put(key);
    }

    public boolean tryAcquireSemaphore(String key) {
        Semaphore semaphore = semaphoreMap.computeIfAbsent(key, k -> new Semaphore(1));
        try {
            return semaphore.tryAcquire(1, 3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void releaseSemaphore(String key) {
        Semaphore semaphore = semaphoreMap.get(key);
        if (semaphore != null) {
            semaphore.release();
        }
    }

    public ReentrantLock getLock(String key) {
        return lockMap.computeIfAbsent(key, k -> new ReentrantLock());
    }

    public boolean tryAcquireToken() {
        return rateLimiter.tryAcquire(1, 1, TimeUnit.SECONDS);
    }

    public long getRandomExpireTime(long baseExpireTime) {
        return baseExpireTime + (long) (Math.random() * randomExpireTimeRange);
    }

    public void addNullValueProtection(String cacheName, String key, long expireTime, TimeUnit timeUnit) {
        bloomFilter.put(key);
    }

    public void resetBloomFilter() {
        bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                expectedInsertions,
                fpp
        );
    }
}
