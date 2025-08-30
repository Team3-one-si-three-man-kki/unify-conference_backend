package com.example.unicon.user.controller;

import com.example.unicon.infrastructure.redis.token.RefreshTokenRepository;
import com.example.unicon.user.dto.EmailCheckRequestDTO;
import com.example.unicon.user.dto.LoginRequestDTO;
import com.example.unicon.user.dto.SignupRequestDTO;
import com.example.unicon.user.dto.UserResponseDTO;
import com.example.unicon.user.service.UserService;
import com.example.unicon.user.vo.UserVO;
import com.example.unicon.util.CookieUtil;
import com.example.unicon.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import com.example.unicon.infrastructure.redis.token.TokenBlacklistRepository; // ◀️ 추가
import org.springframework.security.core.Authentication; // ◀️ 추가
import org.springframework.security.core.context.SecurityContextHolder; // ◀️ 추가
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieUtil cookieUtil;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    // ... signup 메소드는 동일 ...
    @PostMapping("/guest/signup")
    public ResponseEntity<UserResponseDTO> signup(@Valid @RequestBody SignupRequestDTO signupRequest) {
        UserResponseDTO response = userService.processSignup(signupRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/guest/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        UserVO user = userService.processLogin(loginRequest);

        // 2. 토큰 생성 시 userId 대신 email 사용
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getEmail(), // userId -> email
                user.getTenantId().toString(),
                user.getRole(),
                user.isActive()
        );
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail()); // userId -> email

        // 3. Refresh Token 저장 (Redis Key를 email로 사용)
        refreshTokenRepository.save(
                user.getEmail(), // userId -> email
                refreshToken,
                jwtTokenProvider.getRefreshTokenExpiration()
        );

        // 4. 응답 설정 (이전과 동일)
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        long refreshTokenExpirationSeconds = jwtTokenProvider.getRefreshTokenExpiration() / 1000;
        cookieUtil.setRefreshTokenCookie(response, refreshToken, refreshTokenExpirationSeconds);

        // 5. 응답 본문으로 사용자 정보 반환 (이전과 동일)
        UserResponseDTO responseDTO = new UserResponseDTO(user, user.getTenantName(), user.getSubDomain());
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/guest/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@Valid @RequestBody EmailCheckRequestDTO requestDTO) {
        boolean isAvailable = userService.isEmailAvailable(requestDTO.email());
        if (isAvailable) {
            return ResponseEntity.ok(Map.of("isAvailable", true, "message", "사용 가능한 이메일입니다."));
        } else {
            // 409 Conflict 상태 코드는 "이미 리소스가 존재하여 충돌이 발생했다"는 의미로,
            // 중복된 이메일 응답에 적합합니다.
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("isAvailable", false, "message", "이미 사용 중인 이메일입니다."));
        }
    }
    @PostMapping("/user/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. Authorization 헤더에서 Access Token 추출
        String accessToken = jwtTokenProvider.resolveToken(request);

        if (accessToken != null) {
            // 2. Access Token 남은 유효시간 계산하여 Redis 블랙리스트에 추가
            long remainingTime = jwtTokenProvider.getTokenRemainingTime(accessToken);
            if (remainingTime > 0) {
                tokenBlacklistRepository.add(accessToken, remainingTime);
            }
        }

        // 3. SecurityContext에서 사용자 정보(email) 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            // 4. Redis에 저장된 Refresh Token 삭제
            refreshTokenRepository.delete(authentication.getName());
        }

        // 5. 클라이언트의 Refresh Token 쿠키 삭제
        cookieUtil.clearRefreshTokenCookie(response);

        return ResponseEntity.ok(Map.of("message", "성공적으로 로그아웃되었습니다."));
    }

}