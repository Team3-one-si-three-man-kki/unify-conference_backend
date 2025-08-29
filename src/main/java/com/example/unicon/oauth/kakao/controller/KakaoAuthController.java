package com.example.unicon.oauth.kakao.controller;

import com.example.unicon.oauth.kakao.dto.KakaoUserInfoResponse;
import com.example.unicon.oauth.kakao.service.KakaoAuthService;
import com.example.unicon.user.service.UserService;
import com.example.unicon.user.vo.UserVO;
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
import java.util.Optional;

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
    public ResponseEntity<Map<String, String>> getKakaoAuthUrl(@RequestParam String type, @RequestParam String subDomain) {
        // 🔽 state 값에 subDomain을 포함시킴 (예: "login:testcompany")
        String state = type + ":" + subDomain;
        String url = kakaoAuthService.getKakaoLoginUrl(state);
        return ResponseEntity.ok(Map.of("url", url));
    }

    /**
     * 카카오 인증 후 호출되는 콜백 API
     */
    /**
     * 카카오 인증 후 호출되는 콜백 API
     */
    @GetMapping("/callback")
    public void kakaoCallback(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws IOException {
        String redirectUrl;
        try {
            // 🔽 state 값에서 type과 subDomain 분리
            String[] stateParts = state.split(":", 2);
            String type = stateParts[0];
            String subDomain = stateParts.length > 1 ? stateParts[1] : "";

            if (subDomain.isEmpty()) {
                throw new IllegalArgumentException("서브도메인 정보가 없습니다.");
            }

            String accessToken = kakaoAuthService.getAccessToken(code);
            KakaoUserInfoResponse userInfo = kakaoAuthService.getUserInfo(accessToken);

            if (userInfo == null || userInfo.email() == null) {
                throw new RuntimeException("카카오 사용자 정보 조회에 실패했거나 이메일 동의가 필요합니다.");
            }

            if ("signup".equalsIgnoreCase(type)) {
                redirectUrl = handleSignupFlow(userInfo, subDomain); // subDomain 전달
            } else {
                redirectUrl = handleLoginFlow(userInfo, subDomain); // subDomain 전달
            }

        } catch (Exception e) {
            log.error("카카오 콜백 처리 중 오류 발생", e);
            String errorMessage = URLEncoder.encode("카카오 처리 중 오류가 발생했습니다: " + e.getMessage(), StandardCharsets.UTF_8);
            redirectUrl = FRONTEND_LOGIN_URL + "?error=" + errorMessage;
        }
        response.sendRedirect(redirectUrl);
    }

    private String handleLoginFlow(KakaoUserInfoResponse userInfo, String subDomain) throws IOException {
        Optional<UserVO> userOptional = userService.getUserByEmailAndSubdomain(userInfo.email(), subDomain);

        if (userOptional.isPresent()) {
            // TODO: 기존 사용자인 경우, JWT 토큰을 발급하고 대시보드로 리디렉션
            log.info("[{}] 테넌트의 카카오 사용자 로그인 성공: {}", subDomain, userInfo.email());
            return FRONTEND_DASHBOARD_URL + "?message=kakao_login_success";
        } else {
            log.warn("[{}] 테넌트에 등록되지 않은 카카오 사용자 로그인 시도: {}", subDomain, userInfo.email());
            String errorMessage = URLEncoder.encode("해당 테넌트에 등록되지 않은 사용자입니다.", StandardCharsets.UTF_8);
            return FRONTEND_LOGIN_URL + "?tenant=" + subDomain + "&error=" + errorMessage;
        }
    }

    private String handleSignupFlow(KakaoUserInfoResponse userInfo, String subDomain) throws IOException {
        boolean isUserRegistered = !userService.isEmailAvailable(userInfo.email());
        if (isUserRegistered) {
            log.warn("이미 가입된 카카오 사용자 회원가입 시도: {}", userInfo.email());
            String errorMessage = URLEncoder.encode("이미 다른 테넌트에 가입된 이메일입니다. 로그인 해주세요.", StandardCharsets.UTF_8);
            return FRONTEND_SIGNUP_URL + "?error=" + errorMessage;
        } else {
            log.info("신규 카카오 사용자 회원가입 진행: {}", userInfo.email());
            String nickname = URLEncoder.encode(userInfo.nickname(), StandardCharsets.UTF_8);
            String email = URLEncoder.encode(userInfo.email(), StandardCharsets.UTF_8);
            return FRONTEND_SIGNUP_URL + "?mode=kakao&nickname=" + nickname + "&email=" + email;
        }
    }
}