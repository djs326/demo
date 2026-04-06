package com.example.common.aspect;

import com.example.common.annotation.Idempotent;
import com.example.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class IdempotentAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(com.example.common.annotation.Idempotent)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Idempotent idempotent = method.getAnnotation(Idempotent.class);

        String key = generateKey(idempotent.key(), request);
        long expire = idempotent.expire();
        String message = idempotent.message();

        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "1", expire, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(success)) {
            throw new BusinessException(message);
        }

        try {
            return joinPoint.proceed();
        } finally {
            // 不需要删除key，依赖过期时间自动清理
        }
    }

    private String generateKey(String key, HttpServletRequest request) {
        if (StringUtils.hasText(key)) {
            return key;
        }
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String token = request.getHeader("Authorization");
        return "idempotent:" + method + ":" + requestURI + ":" + (token != null ? token : "");
    }
}