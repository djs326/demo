package com.example.common.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

@Service
public class ResilienceService {

    // 熔断示例
    @CircuitBreaker(name = "backendService", fallbackMethod = "fallback")
    @Retry(name = "backendService")
    public String processRequest(String request) {
        // 模拟业务逻辑
        if (request.contains("error")) {
            throw new RuntimeException("Service error");
        }
        return "Processed: " + request;
    }

    // 降级方法
    public String fallback(String request, Exception e) {
        return "Fallback response for: " + request;
    }

    // 限流示例
    @RateLimiter(name = "apiRateLimiter")
    public String limitedApi(String request) {
        return "Limited API response: " + request;
    }
}
