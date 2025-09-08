package com.example.unicon.attendance.controller;

import com.example.unicon.attendance.service.AttendanceService;
import com.example.unicon.attendance.vo.AttendanceRequestVO;
import com.example.unicon.attendance.vo.AttendanceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")  // 개발 환경용, 프로덕션에서는 특정 도메인으로 제한
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * 출석 목록 조회 (React에서 호출하는 메인 API)
     */
    @PostMapping("/list")
    public ResponseEntity<?> getAttendanceList(@RequestBody AttendanceRequestVO request) {
        try {
            AttendanceVO attendanceVo = request.getAttendanceVo();

            if (attendanceVo == null) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("요청 데이터가 올바르지 않습니다."));
            }

            // 빈 문자열을 null로 변환
            cleanEmptyStrings(attendanceVo);

            // 페이징 설정
            setPagination(attendanceVo);

            List<AttendanceVO> attendanceList = attendanceService.selectListAttendance(attendanceVo);
            long totalCount = attendanceService.selectListCountAttendance(attendanceVo);

            // React가 기대하는 응답 형식
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> elData = new HashMap<>();
            elData.put("attendanceVoList", attendanceList);
            response.put("elData", elData);
            response.put("success", true);
            response.put("message", "조회가 완료되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("출석 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 출석 상세 조회
     */
    @PostMapping("/detail")
    public ResponseEntity<?> getAttendanceDetail(@RequestBody AttendanceVO attendanceVo) {
        try {
            if (attendanceVo.getRecordId() == null || attendanceVo.getRecordId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("레코드 ID가 필요합니다."));
            }

            AttendanceVO result = attendanceService.selectAttendance(attendanceVo);

            if (result == null) {
                return ResponseEntity.ok(createErrorResponse("해당 출석 정보를 찾을 수 없습니다."));
            }

            return ResponseEntity.ok(createSuccessResponse(result, "조회가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("출석 상세 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 출석 등록
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAttendance(@RequestBody AttendanceVO attendanceVo) {
        try {
            int result = attendanceService.insertAttendance(attendanceVo);

            if (result > 0) {
                return ResponseEntity.ok(createSuccessResponse(result, "등록이 완료되었습니다."));
            } else {
                return ResponseEntity.ok(createErrorResponse("등록에 실패했습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("출석 등록 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 출석 수정
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateAttendance(@RequestBody AttendanceVO attendanceVo) {
        try {
            int result = attendanceService.updateAttendance(attendanceVo);

            if (result > 0) {
                return ResponseEntity.ok(createSuccessResponse(result, "수정이 완료되었습니다."));
            } else {
                return ResponseEntity.ok(createErrorResponse("수정에 실패했습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("출석 수정 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 출석 삭제
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteAttendance(@RequestBody AttendanceVO attendanceVo) {
        try {
            int result = attendanceService.deleteAttendance(attendanceVo);

            if (result > 0) {
                return ResponseEntity.ok(createSuccessResponse(result, "삭제가 완료되었습니다."));
            } else {
                return ResponseEntity.ok(createErrorResponse("삭제에 실패했습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("출석 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 출석 현황 CSV 다운로드
     */
    @PostMapping("/csv-download")
    public ResponseEntity<?> downloadAttendanceCSV(@RequestBody AttendanceVO attendanceVo) {
        try {
            String csvContent = attendanceService.generateAttendanceCSV(attendanceVo);

            String sessionId = attendanceVo.getScSessionId() != null ?
                    attendanceVo.getScSessionId().replaceAll("[^a-zA-Z0-9]", "_") : "전체";
            String tenantId = attendanceVo.getScTenantId() != null ? attendanceVo.getScTenantId() : "unknown";
            String dateStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = String.format("출석현황_%s_%s_%s.csv", tenantId, sessionId, dateStr);

            byte[] csvBytes = csvContent.getBytes("UTF-8");

            // UTF-8 BOM 추가
            byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            byte[] csvWithBom = new byte[bom.length + csvBytes.length];
            System.arraycopy(bom, 0, csvWithBom, 0, bom.length);
            System.arraycopy(csvBytes, 0, csvWithBom, bom.length, csvBytes.length);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileName, "UTF-8"));
            headers.setContentLength(csvWithBom.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvWithBom);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("CSV 다운로드 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 출석 통계 조회
     */
    @PostMapping("/stats")
    public ResponseEntity<?> getAttendanceStats(@RequestBody AttendanceVO attendanceVo) {
        try {
            AttendanceVO stats = attendanceService.getAttendanceStats(attendanceVo);
            return ResponseEntity.ok(createSuccessResponse(stats, "통계 조회가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("통계 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 활성 참여자 수 조회
     */
    @PostMapping("/active-count")
    public ResponseEntity<?> getActiveParticipantCount(@RequestBody AttendanceVO attendanceVo) {
        try {
            int count = attendanceService.getActiveParticipantCount(attendanceVo);
            Map<String, Object> data = new HashMap<>();
            data.put("activeCount", count);
            return ResponseEntity.ok(createSuccessResponse(data, "활성 참여자 수 조회가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("활성 참여자 수 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 평균 참여시간 조회
     */
    @PostMapping("/avg-time")
    public ResponseEntity<?> getAvgParticipationTime(@RequestBody AttendanceVO attendanceVo) {
        try {
            Double avgTime = attendanceService.getAvgParticipationTime(attendanceVo);
            Map<String, Object> data = new HashMap<>();
            data.put("avgTime", avgTime);
            return ResponseEntity.ok(createSuccessResponse(data, "평균 참여시간 조회가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("평균 참여시간 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 중복 참여자 체크
     */
    @PostMapping("/check-duplicate")
    public ResponseEntity<?> checkDuplicateParticipant(@RequestBody AttendanceVO attendanceVo) {
        try {
            boolean isDuplicate = attendanceService.checkDuplicateParticipant(attendanceVo);
            Map<String, Object> data = new HashMap<>();
            data.put("isDuplicate", isDuplicate);
            return ResponseEntity.ok(createSuccessResponse(data, "중복 참여자 체크가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("중복 참여자 체크 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 기간별 출석 데이터 조회
     */
    @PostMapping("/by-date-range")
    public ResponseEntity<?> getAttendanceByDateRange(@RequestBody AttendanceVO attendanceVo) {
        try {
            List<AttendanceVO> attendanceList = attendanceService.getAttendanceByDateRange(attendanceVo);
            return ResponseEntity.ok(createSuccessResponse(attendanceList, "기간별 출석 데이터 조회가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("기간별 출석 데이터 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // === 기존 웹스퀘어 호환성을 위한 엔드포인트들 (필요시 사용) ===

    @PostMapping("/AttendanceList.pwkjson")
    public ResponseEntity<?> legacyAttendanceList(@RequestBody AttendanceRequestVO request) {
        return getAttendanceList(request);
    }

    @PostMapping("/SVC_ATTENDANCE_CSV_DOWNLOAD.pwkjson")
    public ResponseEntity<?> legacyCSVDownload(@RequestBody AttendanceVO attendanceVo) {
        return downloadAttendanceCSV(attendanceVo);
    }

    @PostMapping("/SVC_ATTENDANCE_STATS.pwkjson")
    public ResponseEntity<?> legacyAttendanceStats(@RequestBody AttendanceVO attendanceVo) {
        return getAttendanceStats(attendanceVo);
    }

    // === 유틸리티 메소드들 ===

    /**
     * 빈 문자열을 null로 변환
     */
    private void cleanEmptyStrings(AttendanceVO attendanceVo) {
        if (attendanceVo.getScSessionId() != null && attendanceVo.getScSessionId().trim().isEmpty()) {
            attendanceVo.setScSessionId(null);
        }
        if (attendanceVo.getScName() != null && attendanceVo.getScName().trim().isEmpty()) {
            attendanceVo.setScName(null);
        }
        if (attendanceVo.getScEmail() != null && attendanceVo.getScEmail().trim().isEmpty()) {
            attendanceVo.setScEmail(null);
        }
        if (attendanceVo.getScJoinTime() != null && attendanceVo.getScJoinTime().trim().isEmpty()) {
            attendanceVo.setScJoinTime(null);
        }
    }

    /**
     * 페이징 설정
     */
    private void setPagination(AttendanceVO attendanceVo) {
        int pageSizeInt = (attendanceVo.getPageSize() > 0) ? attendanceVo.getPageSize() : 9999;
        long pageIndexLong = (attendanceVo.getPageIndex() > 0) ? attendanceVo.getPageIndex() : 1;

        attendanceVo.setPageSize(pageSizeInt);
        attendanceVo.setPageIndex(pageIndexLong);

        int startRow = (int) ((pageIndexLong - 1) * pageSizeInt);
        attendanceVo.setStartRow(startRow);
    }

    /**
     * 에러 응답 생성
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("data", null);
        return response;
    }

    /**
     * 성공 응답 생성
     */
    private Map<String, Object> createSuccessResponse(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }
}
