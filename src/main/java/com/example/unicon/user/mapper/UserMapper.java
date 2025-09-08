package com.example.unicon.user.mapper;

import com.example.unicon.user.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserMapper {

    /**
     * 이메일로 사용자 존재 여부 확인
     */
    Optional<UserVO> findByEmail(String email);

    /**
     * 이메일과 서브도메인으로 사용자 정보 조회 (로그인용)
     */
    Optional<UserVO> findUserWithTenantByEmailAndSubdomain(@Param("email") String email, @Param("subdomain") String subdomain);

    /**
     * 사용자 정보 저장
     */
    void insertUser(UserVO user);

    String selectTenantNameById(Integer tenantId);
}