package com.example.unicon.user.service;

import com.example.unicon.user.dto.LoginRequestDTO;
import com.example.unicon.user.dto.SignupRequestDTO;
import com.example.unicon.user.dto.UserResponseDTO;
import com.example.unicon.user.vo.UserVO;

import java.util.List;
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

    /**
     * 테넌트별 사용자 목록 조회
     */
    List<UserVO> selectUsersByTenant(UserVO vo) throws Exception;

    /**
     * 사용자 목록 저장 (CUD 처리)
     */
    void saveUserList(List<UserVO> userList) throws Exception;

    /**
     * 테넌트 내 이메일 중복 검사
     */
    boolean isEmailAvailableInTenant(String email, String tenantId) throws Exception;

    /**
     * 사용자 등록
     */
    int insertUser(UserVO userVo) throws Exception;

    /**
     * 사용자 수정
     */
    int updateUser(UserVO userVo) throws Exception;

    /**
     * 사용자 삭제
     */
    int deleteUser(UserVO userVo) throws Exception;

    /**
     * 사용자 상세 조회
     */
    UserVO selectUser(UserVO userVo) throws Exception;

    String getTenantNameById(Integer tenantId);
}