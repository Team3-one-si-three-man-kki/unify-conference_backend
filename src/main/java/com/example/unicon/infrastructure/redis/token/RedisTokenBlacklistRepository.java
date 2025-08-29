package com.example.unicon.infrastructure.redis.token;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisTokenBlacklistRepository implements TokenBlacklistRepository {

    private static final String PREFIX = "blacklist:";

    private final RedisTemplate<String, String> redisTemplate;

    public RedisTokenBlacklistRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(String token) {
        // 토큰 원문을 키에 직접 쓰는 방식
        // 필요 시 해시(SHA-256)로 키 길이/노출을 줄이는 것도 고려 가능
        return PREFIX + token;
    }

    @Override
    public void add(String token, long ttlMillis) {
        // 값은 단순 마커 문자열로 저장(예: "logout"). 필요 시 사유/메타데이터 JSON 저장 가능
        redisTemplate.opsForValue().set(key(token), "logout", ttlMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key(token)));
    }

    @Override
    public void remove(String token) {
        redisTemplate.delete(key(token));
    }
}
