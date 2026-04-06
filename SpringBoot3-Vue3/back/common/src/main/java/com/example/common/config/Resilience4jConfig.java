package com.example.common.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    // 限流配置
    @Bean
    public RateLimiterConfig rateLimiterConfig() {
        return RateLimiterConfig.custom()
                .limitForPeriod(10) // 每个周期允许的请求数
                .limitRefreshPeriod(Duration.ofSeconds(1)) // 周期刷新时间
                .timeoutDuration(Duration.ofMillis(500)) // 超时时间
                .build();
    }

    // 熔断配置
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // 失败率阈值
                .slowCallRateThreshold(50) // 慢调用率阈值
                .slowCallDurationThreshold(Duration.ofSeconds(2)) // 慢调用持续时间阈值
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) // 滑动窗口类型
                .slidingWindowSize(100) // 滑动窗口大小
                .minimumNumberOfCalls(10) // 最小调用次数
                .waitDurationInOpenState(Duration.ofSeconds(5)) // 打开状态持续时间
                .permittedNumberOfCallsInHalfOpenState(3) // 半开状态允许的调用次数
                .build();
    }

    // 重试配置
    @Bean
    public RetryConfig retryConfig() {
        return RetryConfig.custom()
                .maxAttempts(3) // 最大重试次数
                .waitDuration(Duration.ofMillis(100)) // 重试等待时间
                .build();
    }

    // 超时配置
    @Bean
    public TimeLimiterConfig timeLimiterConfig() {
        return TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(3)) // 超时时间
                .build();
    }
}
