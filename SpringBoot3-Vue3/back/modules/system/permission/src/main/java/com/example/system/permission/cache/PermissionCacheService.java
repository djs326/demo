package com.example.system.permission.cache;

import com.example.system.permission.entity.SysPermission;
import com.example.system.permission.service.SysPermissionService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PermissionCacheService {
    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 一级缓存：Caffeine本地缓存
    private Cache<Long, List<SysPermission>> permissionCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    // Redis key前缀
    private static final String PERMISSION_KEY_PREFIX = "sys:permission:user:";

    /**
     * 获取用户权限列表（从缓存中获取）
     */
    public List<SysPermission> getPermissionsByUserId(Long userId) {
        // 先从一级缓存获取
        List<SysPermission> permissions = permissionCache.getIfPresent(userId);
        if (permissions != null) {
            return permissions;
        }

        // 再从二级缓存获取
        String redisKey = PERMISSION_KEY_PREFIX + userId;
        permissions = (List<SysPermission>) redisTemplate.opsForValue().get(redisKey);
        if (permissions != null) {
            // 回写一级缓存
            permissionCache.put(userId, permissions);
            return permissions;
        }

        // 从数据库获取
        permissions = permissionService.getPermissionsByUserId(userId);
        if (permissions != null) {
            // 写入二级缓存
            redisTemplate.opsForValue().set(redisKey, permissions, 30, TimeUnit.MINUTES);
            // 写入一级缓存
            permissionCache.put(userId, permissions);
        }

        return permissions;
    }

    /**
     * 清除用户权限缓存
     */
    public void clearPermissionCache(Long userId) {
        // 清除一级缓存
        permissionCache.invalidate(userId);
        // 清除二级缓存
        String redisKey = PERMISSION_KEY_PREFIX + userId;
        redisTemplate.delete(redisKey);
    }

    /**
     * 清除所有权限缓存
     */
    public void clearAllPermissionCache() {
        // 清除一级缓存
        permissionCache.invalidateAll();
        // 清除二级缓存（使用通配符删除所有用户权限缓存）
        redisTemplate.delete(redisTemplate.keys(PERMISSION_KEY_PREFIX + "*"));
    }
}