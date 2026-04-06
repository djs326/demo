package com.example.system.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenVersionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.application.name:common}")
    private String appName;

    @Value("${spring.profiles.active:dev}")
    private String env;

    private static final String TOKEN_VERSION_KEY_PREFIX = "user:token:version:";

    private String getTokenVersionKey(Long userId) {
        return env + ":" + TOKEN_VERSION_KEY_PREFIX + userId;
    }

    public Long getCurrentVersion(Long userId) {
        String key = getTokenVersionKey(userId);
        Object version = redisTemplate.opsForValue().get(key);
        if (version == null) {
            return 1L;
        }
        return (Long) version;
    }

    public void incrementVersion(Long userId) {
        String key = getTokenVersionKey(userId);
        redisTemplate.opsForValue().increment(key);
    }

    public void setVersion(Long userId, Long version) {
        String key = getTokenVersionKey(userId);
        redisTemplate.opsForValue().set(key, version);
    }

    public boolean validateTokenVersion(Long userId, Long tokenVersion) {
        Long currentVersion = getCurrentVersion(userId);
        return tokenVersion.equals(currentVersion);
    }
}
