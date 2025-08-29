package com.example.unicon.infrastructure.redis.token;

public interface RefreshTokenRepository {

    /**
     * Refresh Token을 저장하고 TTL(만료)을 설정합니다.
     * @param userId 사용자 식별자
     * @param refreshToken 저장할 리프레시 토큰
     * @param ttlMillis 만료 시간(밀리초)
     */
    void save(String userId, String refreshToken, long ttlMillis);

    /**
     * 저장된 리프레시 토큰과 전달된 토큰이 일치하는지 확인합니다.
     * @param userId 사용자 식별자
     * @param refreshToken 비교할 리프레시 토큰
     * @return 일치하면 true
     */
    boolean matches(String userId, String refreshToken);

    /**
     * 저장된 리프레시 토큰을 삭제합니다.
     * @param userId 사용자 식별자
     */
    void delete(String userId);
}