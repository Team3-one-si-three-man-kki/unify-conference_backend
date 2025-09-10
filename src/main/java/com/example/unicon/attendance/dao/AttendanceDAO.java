package com.example.unicon.attendance.dao;

import com.example.unicon.attendance.vo.AttendanceVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 출석 관리 DAO
 */
@Mapper
@Repository
public interface AttendanceDAO {

    /**
     * 출석모듈 상세 조회
     */
    AttendanceVO selectAttendance(AttendanceVO vo);

    /**
     * 페이징을 처리하여 출석모듈 목록조회
     */
    List<AttendanceVO> selectListAttendance(AttendanceVO vo);

    /**
     * 출석모듈 목록 조회의 전체 카운트를 조회
     */
    long selectListCountAttendance(AttendanceVO vo);

    /**
     * 출석모듈을 등록
     */
    int insertAttendance(AttendanceVO vo);

    /**
     * 출석모듈을 갱신
     */
    int updateAttendance(AttendanceVO vo);

    /**
     * 출석모듈을 삭제
     */
    int deleteAttendance(AttendanceVO vo);

    /**
     * CSV 다운로드용 출석 목록 조회
     */
    List<AttendanceVO> selectAttendanceForCSV(AttendanceVO vo);

    /**
     * 세션별 출석 통계 조회
     */
    AttendanceVO selectAttendanceStats(AttendanceVO vo);

    // 테넌트별 조회 메소드들

    /**
     * 테넌트별 출석모듈 목록조회 (세션 테이블과 조인)
     */
    List<AttendanceVO> selectListAttendanceByTenant(AttendanceVO vo);

    /**
     * 테넌트별 출석모듈 목록 조회의 전체 카운트를 조회
     */
    long selectListCountAttendanceByTenant(AttendanceVO vo);

    /**
     * 테넌트별 CSV 다운로드용 출석 목록 조회
     */
    List<AttendanceVO> selectAttendanceForCSVByTenant(AttendanceVO vo);

    /**
     * 테넌트별 세션별 출석 통계 조회
     */
    AttendanceVO selectAttendanceStatsByTenant(AttendanceVO vo);

    // 추가 유틸리티 메소드들

    /**
     * 특정 세션의 활성 참여자 수 조회
     */
    int selectActiveParticipantCount(AttendanceVO vo);

    /**
     * 참여자의 평균 참여시간 조회
     */
    Double selectAvgParticipationTime(AttendanceVO vo);

    /**
     * 특정 기간 내 출석 데이터 조회
     */
    List<AttendanceVO> selectAttendanceByDateRange(AttendanceVO vo);

    /**
     * 중복 참여자 체크
     */
    boolean checkDuplicateParticipant(AttendanceVO vo);
}