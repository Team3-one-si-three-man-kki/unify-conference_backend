package com.example.unicon.module.controller;

import com.example.unicon.module.dto.ModuleDetailDto;
import com.example.unicon.module.dto.ModuleUsageDto;
import com.example.unicon.module.service.ModuleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/modules") // MANAGER 또는 ADMIN 권한 필요
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    /**
     * 현재 로그인한 사용자의 테넌트가 보유한 모듈 목록을 조회합니다.
     */
    @GetMapping
    public ResponseEntity<List<ModuleDetailDto>> getTenantModules(HttpServletRequest request) {
        String tenantIdStr = (String) request.getAttribute("tenantId");
        if (tenantIdStr == null) {
            return ResponseEntity.badRequest().build();
        }
        Integer tenantId = Integer.valueOf(tenantIdStr);

        List<ModuleDetailDto> modules = moduleService.getTenantModules(tenantId);
        return ResponseEntity.ok(modules);
    }

    /**
     * 특정 모듈의 사용 내역을 조회합니다.
     */
    @GetMapping("/{moduleId}/usage")
    public ResponseEntity<List<ModuleUsageDto>> getModuleUsage(@PathVariable String moduleId, HttpServletRequest request) {
        // 👇 [수정] Service에 tenantId를 전달합니다.
        String tenantIdStr = (String) request.getAttribute("tenantId");
        if (tenantIdStr == null) {
            return ResponseEntity.badRequest().build();
        }
        Integer tenantId = Integer.valueOf(tenantIdStr);

        List<ModuleUsageDto> usageData = moduleService.getModuleUsage(moduleId, tenantId);
        return ResponseEntity.ok(usageData);
    }
}