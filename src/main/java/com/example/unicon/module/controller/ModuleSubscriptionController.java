package com.example.unicon.module.controller;

import com.example.unicon.module.service.ModuleService;
import com.example.unicon.module.vo.TenantModuleVO;
import com.example.unicon.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/modules")
@CrossOrigin(origins = "*")
public class ModuleSubscriptionController {

    @Resource
    private ModuleService moduleService;

    @Autowired
    private UserService userService;

    /**
     * 현재 로그인한 사용자의 tenantId를 가져오는 헬퍼 메서드
     * WebSquare 방식처럼 단순하게 처리
     */
    private String getCurrentUserTenantId() {
        // WebSquare 방식처럼 간단하게 처리
        // 임시로 tenantId를 1로 설정 (추후 로그인 사용자의 tenantId로 변경)
        return "1";
    }

    /**
     * 모듈 구독 처리
     */
    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, Object>> subscribeModule(
            @RequestBody Map<String, Object> requestBody) {

        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("구독 요청 데이터: " + requestBody);

            Map<String, Object> tenantModuleData = (Map<String, Object>) requestBody.get("tenantModuleVo");

            if (tenantModuleData == null) {
                System.out.println("tenantModuleVo가 null입니다.");
                response.put("success", false);
                response.put("message", "요청 데이터가 올바르지 않습니다.");
                return ResponseEntity.badRequest().body(response);
            }

            System.out.println("tenantModuleData: " + tenantModuleData);

            TenantModuleVO tenantModuleVo = new TenantModuleVO();
            String moduleId = (String) tenantModuleData.get("moduleId");
            tenantModuleVo.setModuleId(moduleId);

            System.out.println("설정된 moduleId: " + moduleId);

            // tenantId 처리 - 기존 WebSquare 방식 적용
            String tenantId = (String) tenantModuleData.get("tenantId");
            System.out.println("요청에서 받은 tenantId: " + tenantId);

            // 기존 방식처럼 tenantId가 없거나 비어있으면 현재 사용자 것으로 설정
            if (tenantId == null || tenantId.trim().isEmpty()) {
                tenantId = getCurrentUserTenantId();
                System.out.println("현재 사용자의 tenantId: " + tenantId);
            }

            tenantModuleVo.setTenantId(tenantId);
            System.out.println("최종 설정된 tenantId: " + tenantModuleVo.getTenantId());

            // 입력값 검증
            if (tenantModuleVo.getModuleId() == null || tenantModuleVo.getModuleId().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "모듈 ID가 필요합니다.");
                return ResponseEntity.badRequest().body(response);
            }

            if (tenantModuleVo.getTenantId() == null || tenantModuleVo.getTenantId().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "테넌트 ID를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }

            System.out.println("서비스 호출 전 - moduleId: " + tenantModuleVo.getModuleId() + ", tenantId: " + tenantModuleVo.getTenantId());

            // 기존 서비스 메서드 호출 (WebSquare와 동일)
            moduleService.subscribeModule(tenantModuleVo);

            response.put("success", true);
            response.put("message", "모듈 구독이 완료되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("구독 처리 중 오류: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "모듈 구독 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 모듈 구독 해지 처리
     */
    @PostMapping("/unsubscribe")
    public ResponseEntity<Map<String, Object>> unsubscribeModule(
            @RequestBody Map<String, Object> requestBody) {

        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> tenantModuleData = (Map<String, Object>) requestBody.get("tenantModuleVo");

            if (tenantModuleData == null) {
                response.put("success", false);
                response.put("message", "요청 데이터가 올바르지 않습니다.");
                return ResponseEntity.badRequest().body(response);
            }

            TenantModuleVO tenantModuleVo = new TenantModuleVO();
            tenantModuleVo.setModuleId((String) tenantModuleData.get("moduleId"));

            // tenantId 처리 - 기존 방식과 동일
            String tenantId = (String) tenantModuleData.get("tenantId");
            if (tenantId == null || tenantId.trim().isEmpty()) {
                tenantId = getCurrentUserTenantId();
            }
            tenantModuleVo.setTenantId(tenantId);

            // 입력값 검증
            if (tenantModuleVo.getModuleId() == null || tenantModuleVo.getModuleId().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "모듈 ID가 필요합니다.");
                return ResponseEntity.badRequest().body(response);
            }

            // 기존 서비스 메서드 호출
            moduleService.unsubscribeModule(tenantModuleVo);

            response.put("success", true);
            response.put("message", "모듈 구독이 해지되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "모듈 구독 해지 중 오류가 발생했습니다.");

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 테넌트의 구독 모듈 목록 조회
     */
    @GetMapping("/subscribed")
    public ResponseEntity<Map<String, Object>> getSubscribedModules(
            @RequestParam(required = false) String tenantId) {

        Map<String, Object> response = new HashMap<>();

        try {
            TenantModuleVO tenantModuleVo = new TenantModuleVO();

            // tenantId 처리 - 기존 방식과 동일
            if (tenantId == null || tenantId.trim().isEmpty()) {
                tenantId = getCurrentUserTenantId();
            }
            tenantModuleVo.setTenantId(tenantId);

            // 기존 서비스 메서드 호출
            List<TenantModuleVO> subscribedModules = moduleService.selectSubscribedModules(tenantModuleVo);

            response.put("success", true);
            response.put("subscribedModules", subscribedModules);
            response.put("totalCount", subscribedModules.size());
            response.put("tenantId", tenantId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("subscribedModules", List.of());
            response.put("totalCount", 0);

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 모듈 구독 상태 확인
     */
    @GetMapping("/subscription-status")
    public ResponseEntity<Map<String, Object>> checkSubscriptionStatus(
            @RequestParam String moduleId,
            @RequestParam(required = false) String tenantId) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 입력값 검증
            if (moduleId == null || moduleId.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "모듈 ID가 필요합니다.");
                return ResponseEntity.badRequest().body(response);
            }

            TenantModuleVO tenantModuleVo = new TenantModuleVO();
            tenantModuleVo.setModuleId(moduleId);

            // tenantId 처리 - 기존 방식과 동일
            if (tenantId == null || tenantId.trim().isEmpty()) {
                tenantId = getCurrentUserTenantId();
            }
            tenantModuleVo.setTenantId(tenantId);

            // 기존 서비스 메서드 호출
            boolean isSubscribed = moduleService.isModuleSubscribed(tenantModuleVo);

            response.put("success", true);
            response.put("isSubscribed", isSubscribed);
            response.put("moduleId", moduleId);
            response.put("tenantId", tenantId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("isSubscribed", false);

            return ResponseEntity.badRequest().body(response);
        }
    }
}