package com.example.unicon.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken, long maxAgeSeconds) {
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true); // JavaScript 접근 차단
        refreshCookie.setSecure(false);   // HTTPS 환경에서만 전송
        refreshCookie.setPath("/");      // 모든 경로에서 사용
        refreshCookie.setMaxAge((int) maxAgeSeconds);
        response.addCookie(refreshCookie);
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0); // 쿠키 즉시 만료
        response.addCookie(refreshCookie);
    }
}