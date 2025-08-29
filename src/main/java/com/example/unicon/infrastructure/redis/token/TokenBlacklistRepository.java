package com.example.unicon.infrastructure.redis.token;

/**
 * Access Token(또는 사용 중지할 토큰) 블랙리스트 저장소
 */
public interface TokenBlacklistRepository {

    /**
     * 토큰을 블랙리스트에 추가 (TTL은 밀리초 단위)
     */
    void add(String token, long ttlMillis);

    /**
     * 토큰이 블랙리스트에 있는지 여부
     */
    boolean isBlacklisted(String token);

    /**
     * 필요 시 블랙리스트에서 제거
     */
    void remove(String token);
}
