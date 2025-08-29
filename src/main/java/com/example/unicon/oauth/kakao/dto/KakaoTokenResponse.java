package com.example.unicon.oauth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// 카카오의 토큰 응답(snake_case)을 Java의 camelCase 필드에 매핑합니다.
public record KakaoTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") Integer expiresIn,
        @JsonProperty("refresh_token_expires_in") Integer refreshTokenExpiresIn
) {}