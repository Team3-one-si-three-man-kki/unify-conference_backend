package com.example.unicon.attendance.mapper;

import com.example.unicon.attendance.vo.AttendanceVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AttendanceMapper {

    // 테넌트별 출석 목록 조회
    List<AttendanceVO> selectListAttendanceByTenant(AttendanceVO attendanceVO);

    // 테넌트별 출석 목록 카운트
    long selectListCountAttendanceByTenant(AttendanceVO attendanceVO);

    // 테넌트별 CSV 다운로드용 출석 목록 조회
    List<AttendanceVO> selectAttendanceForCSVByTenant(AttendanceVO attendanceVO);

    // 테넌트별 출석 통계 조회
    AttendanceVO selectAttendanceStatsByTenant(AttendanceVO attendanceVO);

    // 일반 출석 목록 조회 (테넌트 필터 없음)
    List<AttendanceVO> selectListAttendance(AttendanceVO attendanceVO);

    // 일반 출석 목록 카운트
    long selectListCountAttendance(AttendanceVO attendanceVO);

    // 출석 상세 조회
    AttendanceVO selectAttendance(AttendanceVO attendanceVO);

    // CSV 다운로드용 조회
    List<AttendanceVO> selectAttendanceForCSV(AttendanceVO attendanceVO);

    // 출석 통계 조회
    AttendanceVO selectAttendanceStats(AttendanceVO attendanceVO);

    // 출석 등록
    int insertAttendance(AttendanceVO attendanceVO);

    // 출석 수정
    int updateAttendance(AttendanceVO attendanceVO);

    // 출석 삭제
    int deleteAttendance(AttendanceVO attendanceVO);

    // 활성 참여자 수 조회
    int selectActiveParticipantCount(AttendanceVO attendanceVO);

    // 평균 참여시간 조회
    Double selectAvgParticipationTime(AttendanceVO attendanceVO);

    // 중복 참여자 체크
    int checkDuplicateParticipant(AttendanceVO attendanceVO);

    // 기간별 출석 데이터 조회
    List<AttendanceVO> selectAttendanceByDateRange(AttendanceVO attendanceVO);
}