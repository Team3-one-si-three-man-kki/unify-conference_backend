package com.example.unicon.user.service;

import com.example.unicon.user.vo.UserVo;
import java.util.List;

/**
 * 사용자 관리 서비스 인터페이스
 */
public interface UserService {

    /**
     * 테넌트별 사용자 목록 조회
     */
    List<UserVo> selectUsersByTenant(UserVo vo) throws Exception;

    /**
     * 사용자 목록 저장 (CUD 처리)
     */
    void saveUserList(List<UserVo> userList) throws Exception;

    /**
     * 테넌트 내 이메일 중복 검사
     */
    boolean isEmailAvailableInTenant(String email, String tenantId) throws Exception;

    /**
     * 사용자 등록
     */
    int insertUser(UserVo userVo) throws Exception;

    /**
     * 사용자 수정
     */
    int updateUser(UserVo userVo) throws Exception;

    /**
     * 사용자 삭제
     */
    int deleteUser(UserVo userVo) throws Exception;

    /**
     * 사용자 상세 조회
     */
    UserVo selectUser(UserVo userVo) throws Exception;
}