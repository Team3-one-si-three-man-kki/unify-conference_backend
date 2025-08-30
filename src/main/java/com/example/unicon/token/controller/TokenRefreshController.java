package com.example.unicon.token.controller;

import com.example.unicon.token.service.TokenRefreshService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guest") // 로그인하지 않은 상태에서도 호출 가능해야 하므로 guest 경로 사용
@RequiredArgsConstructor
public class TokenRefreshController {

    private final TokenRefreshService tokenRefreshService;

    @PostMapping("/reissue-token")
    public ResponseEntity<Void> reissueToken(HttpServletRequest request) {
        // 서비스를 통해 새 Access Token 발급
        String newAccessToken = tokenRefreshService.reissueAccessToken(request);

        // 응답 헤더에 새로운 Access Token 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);

        // 본문 없이 헤더만 포함하여 200 OK 응답 반환
        return ResponseEntity.ok().headers(headers).build();
    }
}