package com.example.common.aspect;

import com.example.common.annotation.Cacheable;
import com.example.common.annotation.CachePut;
import com.example.common.annotation.CacheEvict;
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
    public void cacheablePointcut() {
    }

    @Pointcut("@annotation(com.example.common.annotation.CachePut)")
    public void cachePutPointcut() {
    }

    @Pointcut("@annotation(com.example.common.annotation.CacheEvict)")
    public void cacheEvictPointcut() {
    }

    @Around("cacheablePointcut()")
    public Object aroundCacheable(ProceedingJoinPoint joinPoint) throws Throwable {
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

    @Around("cachePutPointcut()")
    public Object aroundCachePut(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CachePut cachePut = method.getAnnotation(CachePut.class);

        String cacheName = cachePut.cacheName();
        String key = generateKey(cachePut.key(), joinPoint);

        Object result = joinPoint.proceed();
        multiLevelCacheService.put(cacheName, key, result);
        return result;
    }

    @Around("cacheEvictPointcut()")
    public Object aroundCacheEvict(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);

        String cacheName = cacheEvict.cacheName();
        String key = generateKey(cacheEvict.key(), joinPoint);
        boolean beforeInvocation = cacheEvict.beforeInvocation();
        boolean allEntries = cacheEvict.allEntries();

        if (beforeInvocation) {
            evictCache(cacheName, key, allEntries);
        }

        Object result = joinPoint.proceed();

        if (!beforeInvocation) {
            evictCache(cacheName, key, allEntries);
        }

        return result;
    }

    private void evictCache(String cacheName, String key, boolean allEntries) {
        if (allEntries) {
            multiLevelCacheService.clear(cacheName);
        } else {
            multiLevelCacheService.evict(cacheName, key);
        }
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
