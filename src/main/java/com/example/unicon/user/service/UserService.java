package com.example.unicon.user.service;

import com.example.unicon.user.dto.LoginRequestDTO;
import com.example.unicon.user.dto.SignupRequestDTO;
import com.example.unicon.user.dto.UserResponseDTO;
import com.example.unicon.user.vo.UserVO;

import java.util.Optional;

public interface UserService {
    /**
     * 회원가입 프로세스 처리
     */
    UserResponseDTO processSignup(SignupRequestDTO signupRequest);

    /**
     * 로그인 프로세스 처리
     */
    UserVO processLogin(LoginRequestDTO loginRequest);

    /**
     * 이메일이 사용 가능한지 확인
     */
    boolean isEmailAvailable(String email);

    /**
     * 카카오 로그인 시 이메일과 서브도메인으로 사용자 정보 조회
     */
    Optional<UserVO> getUserByEmailAndSubdomain(String email, String subDomain);
}