package com.example.common.aspect;

import com.example.common.annotation.RateLimit;
import com.example.common.annotation.RateLimitType;
import com.example.common.exception.BusinessException;
import com.example.common.exception.ErrorCode;
import com.example.common.utils.IpUtils;
import com.example.common.utils.RateLimiterUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Duration;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RateLimiterUtils rateLimiterUtils;

    @Around("@annotation(com.example.common.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        RateLimitType type = rateLimit.type();
        int limit = rateLimit.limit();
        long refreshPeriod = rateLimit.refreshPeriod();
        String message = rateLimit.message();

        boolean allowed = checkRateLimit(type, request, method, limit, refreshPeriod);

        if (!allowed) {
            ErrorCode errorCode = getErrorCode(type);
            throw new BusinessException(errorCode, message);
        }

        return joinPoint.proceed();
    }

    private boolean checkRateLimit(RateLimitType type, HttpServletRequest request, Method method, int limit, long refreshPeriod) {
        Duration duration = Duration.ofSeconds(refreshPeriod);

        switch (type) {
            case IP:
                String ip = IpUtils.getIpAddr(request);
                return rateLimiterUtils.tryAcquireByIp(ip, limit, duration);
            case USER:
                String userId = getUserId(request);
                return rateLimiterUtils.tryAcquireByUser(userId, limit, duration);
            case API:
                String apiPath = request.getRequestURI();
                return rateLimiterUtils.tryAcquireByApi(apiPath, limit, duration);
            case DEFAULT:
            default:
                String defaultKey = method.getDeclaringClass().getName() + ":" + method.getName();
                return rateLimiterUtils.tryAcquireByApi(defaultKey, limit, duration);
        }
    }

    private String getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return token != null ? token : "anonymous";
    }

    private ErrorCode getErrorCode(RateLimitType type) {
        switch (type) {
            case IP:
                return ErrorCode.IP_RATE_LIMIT_EXCEEDED;
            case USER:
                return ErrorCode.USER_RATE_LIMIT_EXCEEDED;
            case API:
                return ErrorCode.API_RATE_LIMIT_EXCEEDED;
            case DEFAULT:
            default:
                return ErrorCode.RATE_LIMIT_EXCEEDED;
        }
    }
}
