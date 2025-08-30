package com.example.unicon.user.dao;

import com.example.unicon.user.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 사용자 관리 DAO
 */
@Mapper
@Repository
public interface UserDAO {

    /**
     * 테넌트별 사용자 목록 조회
     */
    List<UserVo> selectUsersByTenant(UserVo vo);

    /**
     * 이메일과 테넌트로 사용자 조회 (중복 검사용)
     */
    List<UserVo> selectUsersByEmailAndTenant(UserVo searchVo);

    /**
     * 사용자 상세 조회
     */
    UserVo selectUser(UserVo vo);

    /**
     * 사용자 등록
     */
    int insertUser(UserVo vo);

    /**
     * 사용자 수정
     */
    int updateUser(UserVo vo);

    /**
     * 사용자 삭제
     */
    int deleteUser(UserVo vo);

    /**
     * 이메일로 사용자 조회
     */
    UserVo getUserByEmail(@Param("email") String email);

    /**
     * 로그인용 사용자 조회
     */
    UserVo loginUser(UserVo vo);
}