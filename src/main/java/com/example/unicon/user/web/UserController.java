package com.example.unicon.user.web;

import com.example.unicon.user.service.UserService;
import com.example.unicon.user.vo.UserListVo;
import com.example.unicon.user.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 관리 컨트롤러 (Spring Boot 3.x 호환)
 */
@RestController
@RequestMapping("/InsWebApp")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class UserController {

    @Autowired
    private UserService userService;
    
    /**
     * 테넌트별 사용자 목록 조회
     */
    @PostMapping("/TNU0002selectUserList.pwkjson")
    public ResponseEntity<UserListVo> selectUserList(@RequestBody Map<String, Object> requestData) {
        try {
            System.out.println("사용자 목록 조회 요청 받음: " + requestData);

            // 요청 데이터에서 vo 추출
            Map<String, Object> voData = (Map<String, Object>) requestData.get("vo");

            UserVo vo = new UserVo();
            if (voData != null) {
                vo.setTenantId((String) voData.get("tenantId"));
                vo.setSearchKeyword((String) voData.get("searchKeyword"));
            }

            System.out.println("변환된 UserVo: " + vo.toString());

            // 서비스 호출
            List<UserVo> resultList = userService.selectUsersByTenant(vo);

            // 응답 데이터 구성
            UserListVo returnVo = new UserListVo();
            returnVo.setUserVoList(resultList);

            System.out.println("조회 결과 개수: " + (resultList != null ? resultList.size() : 0));

            return ResponseEntity.ok(returnVo);

        } catch (Exception e) {
            System.err.println("사용자 목록 조회 중 오류: " + e.getMessage());
            e.printStackTrace();

            // 에러 응답도 정상적으로 구성
            UserListVo errorVo = new UserListVo();
            errorVo.setUserVoList(new ArrayList<>());
            return ResponseEntity.ok(errorVo);
        }
    }

    /**
     * 사용자 목록 저장 (CUD 처리)
     */
    @PostMapping("/saveUserList.pwkjson")
    public ResponseEntity<Map<String, Object>> saveUserList(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("사용자 저장 요청 받음: " + requestData);

            List<Map<String, Object>> saveDataList = (List<Map<String, Object>>) requestData.get("saveDataList");

            if (saveDataList != null && !saveDataList.isEmpty()) {
                List<UserVo> userList = new ArrayList<>();

                for (Map<String, Object> userData : saveDataList) {
                    UserVo userVo = new UserVo();

                    String userId = (String) userData.get("userId");
                    String rowStatus = (String) userData.get("rowStatus");

                    userVo.setUserId(userId == null || userId.isEmpty() ? null : userId);
                    userVo.setTenantId((String) userData.get("tenantId"));
                    userVo.setName((String) userData.get("name"));
                    userVo.setEmail((String) userData.get("email"));

                    String password = (String) userData.get("password");
                    if (password != null && !password.isEmpty() && !"KEEP_EXISTING_PASSWORD".equals(password)) {
                        userVo.setPassword(password);
                    }

                    userVo.setRole((String) userData.get("role"));
                    userVo.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    userVo.setRowStatus(rowStatus);

                    Object isActiveObj = userData.get("isActive");
                    if (isActiveObj instanceof Boolean) {
                        userVo.setActive((Boolean) isActiveObj);
                    } else {
                        userVo.setActive(true);
                    }

                    userList.add(userVo);
                }

                // 서비스 호출
                userService.saveUserList(userList);

                response.put("success", true);
                response.put("message", "저장이 완료되었습니다.");
                System.out.println("사용자 저장 완료");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("사용자 저장 중 오류: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "저장 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 테넌트별 이메일 중복 검사
     */
    @PostMapping("/checkEmailByTenant.pwkjson")
    public ResponseEntity<Map<String, Object>> checkEmailByTenant(@RequestBody UserVo vo) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("이메일 중복 검사 요청: " + vo.toString());

            String email = vo.getEmail();
            String tenantId = vo.getTenantId();

            boolean available = userService.isEmailAvailableInTenant(email, tenantId);

            response.put("emailCheckResult", available ? "available" : "unavailable");
            response.put("email", email);
            response.put("tenantId", tenantId);

            System.out.println("이메일 중복 검사 결과: " + (available ? "사용가능" : "중복"));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("이메일 중복 검사 중 오류: " + e.getMessage());
            e.printStackTrace();
            response.put("emailCheckResult", "error");
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}