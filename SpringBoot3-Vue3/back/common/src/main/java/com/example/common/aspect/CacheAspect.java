package com.example.common.aspect;

import com.example.common.annotation.Cacheable;
import com.example.common.service.cache.MultiLevelCacheService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

@Aspect
@Component
public class CacheAspect {

    @Autowired
    private MultiLevelCacheService multiLevelCacheService;

    @Pointcut("@annotation(com.example.common.annotation.Cacheable)")
    public void cachePointcut() {
    }

    @Around("cachePointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);

        String cacheName = cacheable.cacheName();
        String key = generateKey(cacheable.key(), joinPoint);

        return multiLevelCacheService.get(cacheName, key, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    return joinPoint.proceed();
                } catch (Throwable e) {
                    throw new Exception(e);
                }
            }
        });
    }

    private String generateKey(String key, ProceedingJoinPoint joinPoint) {
        if (!key.isEmpty()) {
            return key;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(joinPoint.getSignature().getDeclaringTypeName());
        sb.append(".");
        sb.append(joinPoint.getSignature().getName());
        sb.append("(");

        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(args[i]);
        }
        sb.append(")");

        return sb.toString();
    }
}
