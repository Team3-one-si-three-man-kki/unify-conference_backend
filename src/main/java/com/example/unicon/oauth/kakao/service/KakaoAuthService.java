package com.example.unicon.oauth.kakao.service;

import com.example.unicon.oauth.kakao.dto.KakaoTokenResponse;
import com.example.unicon.oauth.kakao.dto.KakaoUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 카카오 로그인/회원가입 페이지 URL 생성
     * @param type "login" 또는 "signup"을 state 값으로 전달하여 콜백에서 구분
     */
    public String getKakaoLoginUrl(String type) {
        return "https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoRedirectUri +
                "&response_type=code" +
                "&state=" + type; // state 파라미터로 로그인/회원가입 구분
    }

    /**
     * 인가 코드로 카카오 Access Token 발급
     */
    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);
        params.add("client_secret", kakaoClientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        KakaoTokenResponse response = restTemplate.postForObject("https://kauth.kakao.com/oauth/token", request, KakaoTokenResponse.class);

        return response != null ? response.accessToken() : null;
    }

    /**
     * Access Token으로 카카오 사용자 정보 조회
     */
    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        Map<String, Object> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                Map.class
        ).getBody();

        return response != null ? new KakaoUserInfoResponse(response) : null;
    }
}