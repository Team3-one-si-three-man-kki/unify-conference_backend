package com.example.unicon.domain.tenantModule.controller;

import com.example.unicon.domain.tenantModule.dto.TenantModuleDto;
import com.example.unicon.domain.tenantModule.service.TenantModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/user/tenant_module")
@RequiredArgsConstructor
public class TenantModuleController {
    private final TenantModuleService tenantModuleService;

    @GetMapping
    public ResponseEntity<List<TenantModuleDto>> getTenantModule(HttpServletRequest request) {
        String tenantIdStr = (String) request.getAttribute("tenantId");
        if (tenantIdStr == null) {
            return ResponseEntity.badRequest().build();
        }
        Integer tenantId = Integer.valueOf(tenantIdStr);

        List<TenantModuleDto> tenantModuleDtoList = tenantModuleService.getTenantModules(tenantId);

        return ResponseEntity.ok(tenantModuleDtoList);
    }
}
