package com.example.system.permission.cache;

import com.example.system.permission.entity.SysFieldPermission;
import com.example.system.permission.service.SysFieldPermissionService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class FieldPermissionCacheService {
    @Autowired
    private SysFieldPermissionService fieldPermissionService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Cache<String, Map<String, Map<String, String>>> fieldPermissionCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    private static final String FIELD_PERMISSION_KEY_PREFIX = "sys:field:permission:role:";

    public Map<String, Map<String, String>> getFieldPermissionsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return null;
        }

        String cacheKey = String.join("_", roleIds.stream().map(String::valueOf).toArray(String[]::new));
        
        Map<String, Map<String, String>> permissions = fieldPermissionCache.getIfPresent(cacheKey);
        if (permissions != null) {
            return permissions;
        }

        String redisKey = FIELD_PERMISSION_KEY_PREFIX + cacheKey;
        permissions = (Map<String, Map<String, String>>) redisTemplate.opsForValue().get(redisKey);
        if (permissions != null) {
            fieldPermissionCache.put(cacheKey, permissions);
            return permissions;
        }

        permissions = fieldPermissionService.getFieldPermissionMapByRoleIds(roleIds);
        if (permissions != null) {
            redisTemplate.opsForValue().set(redisKey, permissions, 30, TimeUnit.MINUTES);
            fieldPermissionCache.put(cacheKey, permissions);
        }

        return permissions;
    }

    public void clearFieldPermissionCache(List<Long> roleIds) {
        String cacheKey = String.join("_", roleIds.stream().map(String::valueOf).toArray(String[]::new));
        fieldPermissionCache.invalidate(cacheKey);
        
        String redisKey = FIELD_PERMISSION_KEY_PREFIX + cacheKey;
        redisTemplate.delete(redisKey);
    }

    public void clearAllFieldPermissionCache() {
        fieldPermissionCache.invalidateAll();
        redisTemplate.delete(redisTemplate.keys(FIELD_PERMISSION_KEY_PREFIX + "*"));
    }
}
