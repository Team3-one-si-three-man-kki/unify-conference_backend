package com.example.unicon.token.service;

import com.example.unicon.infrastructure.redis.token.RefreshTokenRepository;
import com.example.unicon.user.mapper.UserMapper;
import com.example.unicon.user.vo.UserVO;
import com.example.unicon.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenRefreshServiceImpl implements TokenRefreshService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;

    @Override
    public String reissueAccessToken(HttpServletRequest request) {
        // 1. HttpOnly 쿠키에서 Refresh Token 추출
        String refreshToken = extractRefreshToken(request)
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token 쿠키가 없습니다."));

        // 2. Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 Refresh Token입니다.");
        }

        // 3. 토큰에서 이메일을 추출하고 Redis의 토큰과 일치하는지 확인
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        if (!refreshTokenRepository.matches(email, refreshToken)) {
            throw new IllegalArgumentException("저장소의 Refresh Token과 일치하지 않습니다.");
        }

        // 4. 이메일로 사용자 정보 조회
        UserVO user = userMapper.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        // 5. 새로운 Access Token 생성 및 반환
        return jwtTokenProvider.generateAccessToken(
                user.getEmail(),
                user.getTenantId().toString(),
                user.getRole(),
                user.isActive()
        );
    }

    private Optional<String> extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}