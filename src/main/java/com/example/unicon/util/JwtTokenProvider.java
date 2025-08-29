package com.example.unicon.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@Getter
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Value("${jwt.issuer}")
    private String issuer;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Access Token 생성 (첫 번째 파라미터를 email로 변경)
    public String generateAccessToken(String email, String tenantId, String role, boolean isActive) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(email) // subject를 email로 설정
                .claim("type", "access")
                .claim("tenantId", tenantId)
                .claim("role", role)
                .claim("isActive", isActive)
                .setIssuer(issuer).setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(accessTokenExpiration, ChronoUnit.MILLIS)))
                .signWith(getSigningKey())
                .compact();
    }

    // Refresh Token 생성 (파라미터를 email로 변경)
    public String generateRefreshToken(String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(email) // subject를 email로 설정
                .claim("type", "refresh")
                .setIssuer(issuer).setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(refreshTokenExpiration, ChronoUnit.MILLIS)))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰에서 email 추출 (메소드 이름 변경)
    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    // (기타 validateToken, getIsActiveFromToken 등의 메소드는 이전과 동일)
    public boolean validateToken(String token) {
        if (token.isEmpty()) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()).build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Boolean getIsActiveFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("isActive", Boolean.class);
    }

    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    public String getTenantIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("tenantId", String.class);
    }

    public long getTokenRemainingTime(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            long currentTime = System.currentTimeMillis();
            long expirationTime = expiration.getTime();

            return Math.max(0, expirationTime - currentTime);
        } catch (Exception e) {
            return 0;
        }
    }
}