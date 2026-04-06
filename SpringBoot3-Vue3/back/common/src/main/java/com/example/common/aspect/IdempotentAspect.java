package com.example.common.aspect;

import com.example.common.annotation.Idempotent;
import com.example.common.exception.BusinessException;
import com.example.common.exception.ErrorCode;
import com.example.common.utils.IdempotentKeyGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class IdempotentAspect {

    private static final Logger log = LoggerFactory.getLogger(IdempotentAspect.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(com.example.common.annotation.Idempotent)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Idempotent idempotent = method.getAnnotation(Idempotent.class);

        String key;
        try {
            key = IdempotentKeyGenerator.generate(joinPoint, idempotent);
        } catch (Exception e) {
            log.error("Failed to generate idempotent key", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成幂等Key失败");
        }

        long expire = idempotent.expire();
        String message = idempotent.message();

        log.debug("Idempotent check, key: {}, expire: {}s", key, expire);

        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "1", expire, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(success)) {
            log.warn("Duplicate request detected, key: {}", key);
            throw new BusinessException(ErrorCode.IDEMPOTENT_ERROR, message);
        }

        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("Method execution failed, removing idempotent key: {}", key);
            redisTemplate.delete(key);
            throw e;
        }
    }
}