package com.example.unicon.attendance.service;

import com.example.unicon.attendance.dao.AttendanceDAO;
import com.example.unicon.attendance.vo.AttendanceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@Service("attendanceServiceImpl")
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    @Autowired
    private AttendanceDAO attendanceDAO;

    /**
     * 출석모듈 목록을 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceVO> selectListAttendance(AttendanceVO attendanceVo) throws Exception {
        try {
            logger.debug("출석 목록 조회 시작: {}", attendanceVo.toString());

            // 테넌트 ID가 있으면 테넌트별 조회, 없으면 일반 조회
            if (attendanceVo.getScTenantId() != null && !attendanceVo.getScTenantId().trim().isEmpty()) {
                logger.debug("테넌트별 출석 목록 조회: {}", attendanceVo.getScTenantId());
                return attendanceDAO.selectListAttendanceByTenant(attendanceVo);
            } else {
                logger.debug("일반 출석 목록 조회");
                return attendanceDAO.selectListAttendance(attendanceVo);
            }
        } catch (Exception e) {
            logger.error("출석 목록 조회 중 오류 발생", e);
            throw new Exception("출석 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 조회한 출석모듈 전체 카운트
     */
    @Override
    @Transactional(readOnly = true)
    public long selectListCountAttendance(AttendanceVO attendanceVo) throws Exception {
        try {
            if (attendanceVo.getScTenantId() != null && !attendanceVo.getScTenantId().trim().isEmpty()) {
                return attendanceDAO.selectListCountAttendanceByTenant(attendanceVo);
            } else {
                return attendanceDAO.selectListCountAttendance(attendanceVo);
            }
        } catch (Exception e) {
            logger.error("출석 카운트 조회 중 오류 발생", e);
            throw new Exception("출석 카운트 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 출석모듈을 상세 조회
     */
    @Override
    @Transactional(readOnly = true)
    public AttendanceVO selectAttendance(AttendanceVO attendanceVo) throws Exception {
        try {
            if (attendanceVo.getRecordId() == null || attendanceVo.getRecordId().trim().isEmpty()) {
                throw new IllegalArgumentException("조회할 레코드 ID가 필요합니다.");
            }
            return attendanceDAO.selectAttendance(attendanceVo);
        } catch (Exception e) {
            logger.error("출석 상세 조회 중 오류 발생", e);
            throw new Exception("출석 상세 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 출석모듈을 등록 처리
     */
    @Override
    public int insertAttendance(AttendanceVO attendanceVo) throws Exception {
        try {
            // 등록 전 유효성 검사
            validateAttendanceVo(attendanceVo);

            // recordId가 없으면 UUID 생성
            if (attendanceVo.getRecordId() == null || attendanceVo.getRecordId().trim().isEmpty()) {
                attendanceVo.setRecordId(UUID.randomUUID().toString());
            }

            // 중복 참여자 체크
            if (checkDuplicateParticipant(attendanceVo)) {
                throw new IllegalArgumentException("이미 해당 세션에 참여 중인 이메일입니다.");
            }

            return attendanceDAO.insertAttendance(attendanceVo);
        } catch (Exception e) {
            logger.error("출석 등록 중 오류 발생", e);
            throw new Exception("출석 등록 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 출석모듈을 갱신 처리
     */
    @Override
    public int updateAttendance(AttendanceVO attendanceVo) throws Exception {
        try {
            // 수정 전 유효성 검사
            validateAttendanceVo(attendanceVo);

            if (attendanceVo.getRecordId() == null || attendanceVo.getRecordId().trim().isEmpty()) {
                throw new IllegalArgumentException("수정할 레코드 ID가 필요합니다.");
            }

            return attendanceDAO.updateAttendance(attendanceVo);
        } catch (Exception e) {
            logger.error("출석 수정 중 오류 발생", e);
            throw new Exception("출석 수정 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 출석모듈을 삭제 처리
     */
    @Override
    public int deleteAttendance(AttendanceVO attendanceVo) throws Exception {
        try {
            if (attendanceVo.getRecordId() == null || attendanceVo.getRecordId().trim().isEmpty()) {
                throw new IllegalArgumentException("삭제할 레코드 ID가 필요합니다.");
            }

            return attendanceDAO.deleteAttendance(attendanceVo);
        } catch (Exception e) {
            logger.error("출석 삭제 중 오류 발생", e);
            throw new Exception("출석 삭제 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String generateAttendanceCSV(AttendanceVO searchVo) throws Exception {
        try {
            List<AttendanceVO> attendanceList;

            if (searchVo.getScTenantId() != null && !searchVo.getScTenantId().trim().isEmpty()) {
                attendanceList = attendanceDAO.selectAttendanceForCSVByTenant(searchVo);
            } else {
                attendanceList = attendanceDAO.selectAttendanceForCSV(searchVo);
            }

            StringBuilder csvContent = new StringBuilder();
            // UTF-8 BOM 추가하지 않음 (Controller에서 처리)
            csvContent.append("번호,세션명,참여자명,이메일,접속IP,입장시간,퇴장시간,참여시간(분),상태\n");

            int rowNumber = 1;
            for (AttendanceVO attendance : attendanceList) {
                csvContent.append(rowNumber++).append(",");
                csvContent.append(escapeCSV(attendance.getSessionName())).append(",");
                csvContent.append(escapeCSV(attendance.getName())).append(",");
                csvContent.append(escapeCSV(attendance.getEmail())).append(",");
                csvContent.append(escapeCSV(attendance.getIpAddress())).append(",");
                csvContent.append(escapeCSV(attendance.getJoinTime())).append(",");
                csvContent.append(escapeCSV(attendance.getLeaveTime() != null ? attendance.getLeaveTime() : "진행중")).append(",");
                csvContent.append(escapeCSV(attendance.getParticipationMinutes())).append(",");
                csvContent.append(escapeCSV(attendance.getStatus()));
                csvContent.append("\n");
            }

            return csvContent.toString();
        } catch (Exception e) {
            logger.error("CSV 생성 중 오류 발생", e);
            throw new Exception("CSV 생성 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceVO getAttendanceStats(AttendanceVO searchVo) throws Exception {
        try {
            if (searchVo.getScTenantId() != null && !searchVo.getScTenantId().trim().isEmpty()) {
                return attendanceDAO.selectAttendanceStatsByTenant(searchVo);
            } else {
                return attendanceDAO.selectAttendanceStats(searchVo);
            }
        } catch (Exception e) {
            logger.error("출석 통계 조회 중 오류 발생", e);
            throw new Exception("출석 통계 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * CSV 값 이스케이프 처리
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        // 쉼표, 따옴표, 개행문자가 포함된 경우 따옴표로 감싸고 내부 따옴표는 이스케이프
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    /**
     * AttendanceVO 유효성 검사
     */
    private void validateAttendanceVo(AttendanceVO attendanceVo) throws IllegalArgumentException {
        if (attendanceVo == null) {
            throw new IllegalArgumentException("출석 정보가 필요합니다.");
        }

        if (attendanceVo.getSessionId() == null || attendanceVo.getSessionId().trim().isEmpty()) {
            throw new IllegalArgumentException("세션 ID가 필요합니다.");
        }

        if (attendanceVo.getName() == null || attendanceVo.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("참여자명이 필요합니다.");
        }

        if (attendanceVo.getEmail() == null || attendanceVo.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("이메일이 필요합니다.");
        }

        // 이메일 형식 검증 (더 엄격한 정규식 사용)
        if (!attendanceVo.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
    }

    /**
     * 활성 참여자 수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public int getActiveParticipantCount(AttendanceVO attendanceVo) throws Exception {
        try {
            return attendanceDAO.selectActiveParticipantCount(attendanceVo);
        } catch (Exception e) {
            logger.error("활성 참여자 수 조회 중 오류 발생", e);
            throw new Exception("활성 참여자 수 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 평균 참여시간 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Double getAvgParticipationTime(AttendanceVO attendanceVo) throws Exception {
        try {
            return attendanceDAO.selectAvgParticipationTime(attendanceVo);
        } catch (Exception e) {
            logger.error("평균 참여시간 조회 중 오류 발생", e);
            throw new Exception("평균 참여시간 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 중복 참여자 체크
     */
    @Override
    @Transactional(readOnly = true)
    public boolean checkDuplicateParticipant(AttendanceVO attendanceVo) throws Exception {
        try {
            return attendanceDAO.checkDuplicateParticipant(attendanceVo);
        } catch (Exception e) {
            logger.error("중복 참여자 체크 중 오류 발생", e);
            throw new Exception("중복 참여자 체크 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 특정 기간 내 출석 데이터 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceVO> getAttendanceByDateRange(AttendanceVO attendanceVo) throws Exception {
        try {
            return attendanceDAO.selectAttendanceByDateRange(attendanceVo);
        } catch (Exception e) {
            logger.error("기간별 출석 데이터 조회 중 오류 발생", e);
            throw new Exception("기간별 출석 데이터 조회 중 오류가 발생했습니다.", e);
        }
    }
}