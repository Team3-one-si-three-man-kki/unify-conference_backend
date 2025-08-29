package com.example.unicon.oauth.kakao.dto;

import java.util.Map;

// 카카오의 중첩된 사용자 정보 응답에서 필요한 데이터만 추출합니다.
public record KakaoUserInfoResponse(
        String id,
        String email,
        String nickname
) {
    // Jackson이 JSON을 Map으로 변환하면, 이 생성자를 통해 KakaoUserInfoResponse 객체를 만듭니다.
    @SuppressWarnings("unchecked")
    public KakaoUserInfoResponse(Map<String, Object> attributes) {
        this(
                String.valueOf(attributes.get("id")),
                (String) ((Map<String, Object>) attributes.get("kakao_account")).get("email"),
                (String) ((Map<String, Object>) ((Map<String, Object>) attributes.get("kakao_account")).get("profile")).get("nickname")
        );
    }
}