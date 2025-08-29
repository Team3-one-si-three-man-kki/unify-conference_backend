package com.example.unicon.infrastructure.redis.token;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private static final String PREFIX = "refresh:";

    private final RedisTemplate<String, String> redisTemplate;

    public RedisRefreshTokenRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(String userId) {
        return PREFIX + userId;
    }

    @Override
    public void save(String userId, String refreshToken, long ttlMillis) {
        String k = key(userId);
        redisTemplate.opsForValue().set(k, refreshToken, ttlMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean matches(String userId, String refreshToken) {
        String stored = redisTemplate.opsForValue().get(key(userId));
        return stored != null && stored.equals(refreshToken);
    }

    @Override
    public void delete(String userId) {
        redisTemplate.delete(key(userId));
    }
}