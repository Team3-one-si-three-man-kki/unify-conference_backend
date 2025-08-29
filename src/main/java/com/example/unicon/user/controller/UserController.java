package com.example.unicon.user.controller;

import com.example.unicon.infrastructure.redis.token.RefreshTokenRepository;
import com.example.unicon.user.dto.LoginRequestDTO;
import com.example.unicon.user.dto.SignupRequestDTO;
import com.example.unicon.user.dto.UserResponseDTO;
import com.example.unicon.user.service.UserService;
import com.example.unicon.user.vo.UserVO;
import com.example.unicon.util.CookieUtil;
import com.example.unicon.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieUtil cookieUtil;

    // ... signup 메소드는 동일 ...
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signup(@Valid @RequestBody SignupRequestDTO signupRequest) {
        UserResponseDTO response = userService.processSignup(signupRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
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
}