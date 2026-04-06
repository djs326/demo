package com.example.system.permission.cache;

import com.example.system.permission.entity.SysPermission;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PermissionCacheServiceImpl implements PermissionCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private LoadingCache<String, List<SysPermission>> caffeineCache;

    public PermissionCacheServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        // 初始化Caffeine缓存
        caffeineCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build(key -> null);
    }

    private String getPermissionKey(Long userId) {
        return "prod:user:perm:" + userId;
    }

    @Override
    public List<SysPermission> getPermissionsByUserId(Long userId) {
        String key = getPermissionKey(userId);
        
        // 先从Caffeine缓存获取
        List<SysPermission> permissions = caffeineCache.get(key);
        if (permissions != null) {
            return permissions;
        }
        
        // 再从Redis缓存获取
        permissions = (List<SysPermission>) redisTemplate.opsForValue().get(key);
        if (permissions != null) {
            // 回写到Caffeine缓存
            caffeineCache.put(key, permissions);
            return permissions;
        }
        
        return null;
    }

    @Override
    public void setPermissionsByUserId(Long userId, List<SysPermission> permissions) {
        String key = getPermissionKey(userId);
        
        // 写入Redis缓存，设置30分钟过期
        redisTemplate.opsForValue().set(key, permissions, 30, TimeUnit.MINUTES);
        
        // 写入Caffeine缓存
        caffeineCache.put(key, permissions);
    }

    @Override
    public void clearByUserId(Long userId) {
        String key = getPermissionKey(userId);
        
        // 清除Redis缓存
        redisTemplate.delete(key);
        
        // 清除Caffeine缓存
        caffeineCache.invalidate(key);
    }

    @Override
    public void clearAll() {
        // 清除所有Caffeine缓存
        caffeineCache.invalidateAll();
        
        // 清除Redis中所有权限缓存（使用keys命令可能影响性能，实际生产环境建议使用Redis的SCAN命令）
        redisTemplate.delete(redisTemplate.keys("prod:user:perm:*"));
    }
}