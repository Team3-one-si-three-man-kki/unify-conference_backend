package com.example.unicon.attendance.service;

import com.example.unicon.attendance.vo.AttendanceVO;

import java.util.List;

public interface AttendanceService {

    /**
     * 출석모듈 페이징 처리하여 목록을 조회
     */
    List<AttendanceVO> selectListAttendance(AttendanceVO attendanceVo) throws Exception;

    /**
     * 조회한 출석모듈 전체 카운트
     */
    long selectListCountAttendance(AttendanceVO attendanceVo) throws Exception;

    /**
     * 출석모듈을 상세 조회
     */
    AttendanceVO selectAttendance(AttendanceVO attendanceVo) throws Exception;

    /**
     * 출석모듈을 등록 처리
     */
    int insertAttendance(AttendanceVO attendanceVo) throws Exception;

    /**
     * 출석모듈을 갱신 처리
     */
    int updateAttendance(AttendanceVO attendanceVo) throws Exception;

    /**
     * 출석모듈을 삭제 처리
     */
    int deleteAttendance(AttendanceVO attendanceVo) throws Exception;

    /**
     * CSV 콘텐츠 문자열 생성
     */
    String generateAttendanceCSV(AttendanceVO searchVo) throws Exception;

    /**
     * 출석 통계 정보 조회
     */
    AttendanceVO getAttendanceStats(AttendanceVO searchVo) throws Exception;

    /**
     * 활성 참여자 수 조회
     */
    int getActiveParticipantCount(AttendanceVO attendanceVo) throws Exception;

    /**
     * 평균 참여시간 조회
     */
    Double getAvgParticipationTime(AttendanceVO attendanceVo) throws Exception;

    /**
     * 중복 참여자 체크
     */
    boolean checkDuplicateParticipant(AttendanceVO attendanceVo) throws Exception;

    /**
     * 특정 기간 내 출석 데이터 조회
     */
    List<AttendanceVO> getAttendanceByDateRange(AttendanceVO attendanceVo) throws Exception;
}