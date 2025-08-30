package com.example.unicon.user.dao;

import com.example.unicon.user.vo.UserVO;
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
    List<UserVO> selectUsersByTenant(UserVO vo);

    /**
     * 이메일과 테넌트로 사용자 조회 (중복 검사용)
     */
    List<UserVO> selectUsersByEmailAndTenant(UserVO searchVo);

    /**
     * 사용자 상세 조회
     */
    UserVO selectUser(UserVO vo);

    /**
     * 사용자 등록
     */
    int insertUser(UserVO vo);

    /**
     * 사용자 수정
     */
    int updateUser(UserVO vo);

    /**
     * 사용자 삭제
     */
    int deleteUser(UserVO vo);

    /**
     * 이메일로 사용자 조회
     */
    UserVO getUserByEmail(@Param("email") String email);

    /**
     * 로그인용 사용자 조회
     */
    UserVO loginUser(UserVO vo);
}