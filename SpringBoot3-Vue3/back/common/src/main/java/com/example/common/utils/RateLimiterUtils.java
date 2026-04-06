package com.example.common.utils;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterUtils {

    private final RateLimiterRegistry rateLimiterRegistry;
    private final Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    @Autowired
    public RateLimiterUtils(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    // IP限流
    public boolean tryAcquireByIp(String ip, int limit, Duration refreshPeriod) {
        String key = "ip:" + ip;
        RateLimiter rateLimiter = rateLimiterMap.computeIfAbsent(key, k -> {
            RateLimiterConfig config = RateLimiterConfig.custom()
                    .limitForPeriod(limit)
                    .limitRefreshPeriod(refreshPeriod)
                    .build();
            return rateLimiterRegistry.rateLimiter(key, config);
        });
        return rateLimiter.acquirePermission();
    }

    // 用户限流
    public boolean tryAcquireByUser(String userId, int limit, Duration refreshPeriod) {
        String key = "user:" + userId;
        RateLimiter rateLimiter = rateLimiterMap.computeIfAbsent(key, k -> {
            RateLimiterConfig config = RateLimiterConfig.custom()
                    .limitForPeriod(limit)
                    .limitRefreshPeriod(refreshPeriod)
                    .build();
            return rateLimiterRegistry.rateLimiter(key, config);
        });
        return rateLimiter.acquirePermission();
    }

    // 接口级限流
    public boolean tryAcquireByApi(String apiPath, int limit, Duration refreshPeriod) {
        String key = "api:" + apiPath;
        RateLimiter rateLimiter = rateLimiterMap.computeIfAbsent(key, k -> {
            RateLimiterConfig config = RateLimiterConfig.custom()
                    .limitForPeriod(limit)
                    .limitRefreshPeriod(refreshPeriod)
                    .build();
            return rateLimiterRegistry.rateLimiter(key, config);
        });
        return rateLimiter.acquirePermission();
    }
}
