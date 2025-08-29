package com.example.unicon.oauth.kakao.controller;

import com.example.unicon.oauth.kakao.dto.KakaoUserInfoResponse;
import com.example.unicon.oauth.kakao.service.KakaoAuthService;
import com.example.unicon.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/guest/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    private final UserService userService;

    // 프론트엔드 주소 (Vite 기본 포트)
    private final String FRONTEND_SIGNUP_URL = "http://localhost:5173/signup";
    private final String FRONTEND_LOGIN_URL = "http://localhost:5173/login"; // 로그인 페이지 경로
    private final String FRONTEND_DASHBOARD_URL = "http://localhost:5173/"; // 대시보드 경로

    /**
     * 카카오 로그인/회원가입 URL을 프론트에 제공
     */
    @GetMapping("/auth-url")
    public ResponseEntity<Map<String, String>> getKakaoAuthUrl(@RequestParam String type) {
        String url = kakaoAuthService.getKakaoLoginUrl(type);
        return ResponseEntity.ok(Map.of("url", url));
    }

    /**
     * 카카오 인증 후 호출되는 콜백 API
     */
    @GetMapping("/callback")
    public void kakaoCallback(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws IOException {
        String redirectUrl;
        try {
            String accessToken = kakaoAuthService.getAccessToken(code);
            KakaoUserInfoResponse userInfo = kakaoAuthService.getUserInfo(accessToken);

            if (userInfo == null || userInfo.email() == null) {
                throw new RuntimeException("카카오 사용자 정보 조회에 실패했거나 이메일 동의가 필요합니다.");
            }

            // state 값에 따라 로그인 또는 회원가입 로직 분기
            if ("signup".equalsIgnoreCase(state)) {
                redirectUrl = handleSignupFlow(userInfo);
            } else {
                redirectUrl = handleLoginFlow(userInfo);
            }

        } catch (Exception e) {
            log.error("카카오 콜백 처리 중 오류 발생", e);
            String errorMessage = URLEncoder.encode("카카오 처리 중 오류가 발생했습니다: " + e.getMessage(), StandardCharsets.UTF_8);
            redirectUrl = FRONTEND_LOGIN_URL + "?error=" + errorMessage;
        }
        response.sendRedirect(redirectUrl);
    }

    private String handleLoginFlow(KakaoUserInfoResponse userInfo) throws IOException {
        boolean isUserRegistered = !userService.isEmailAvailable(userInfo.email());
        if (isUserRegistered) {
            // TODO: 기존 사용자인 경우, JWT 토큰을 발급하고 대시보드로 리디렉션
            // 이 부분은 기존 로그인 로직과 통합이 필요합니다.
            log.info("기존 카카오 사용자 로그인 성공: {}", userInfo.email());
            return FRONTEND_DASHBOARD_URL + "?message=kakao_login_success"; // 임시 리디렉션
        } else {
            // 가입되지 않은 사용자
            log.warn("등록되지 않은 카카오 사용자 로그인 시도: {}", userInfo.email());
            String errorMessage = URLEncoder.encode("등록되지 않은 사용자입니다. 먼저 회원가입을 진행해주세요.", StandardCharsets.UTF_8);
            return FRONTEND_LOGIN_URL + "?error=" + errorMessage;
        }
    }

    private String handleSignupFlow(KakaoUserInfoResponse userInfo) throws IOException {
        boolean isUserRegistered = !userService.isEmailAvailable(userInfo.email());
        if (isUserRegistered) {
            // 이미 가입된 사용자
            log.warn("이미 가입된 카카오 사용자 회원가입 시도: {}", userInfo.email());
            String errorMessage = URLEncoder.encode("이미 가입된 이메일입니다. 로그인 해주세요.", StandardCharsets.UTF_8);
            return FRONTEND_SIGNUP_URL + "?error=" + errorMessage;
        } else {
            // 신규 사용자: 이름과 이메일을 파라미터로 담아 회원가입 페이지로 리디렉션
            log.info("신규 카카오 사용자 회원가입 진행: {}", userInfo.email());
            String nickname = URLEncoder.encode(userInfo.nickname(), StandardCharsets.UTF_8);
            String email = URLEncoder.encode(userInfo.email(), StandardCharsets.UTF_8);
            return FRONTEND_SIGNUP_URL + "?mode=kakao&nickname=" + nickname + "&email=" + email;
        }
    }
}