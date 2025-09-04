package com.example.unicon.domain.tenantModule.service;

import com.example.unicon.domain.tenantModule.dto.TenantModuleDto;
import com.example.unicon.domain.tenantModule.mapper.TenantModuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantModuleService {

    private final TenantModuleMapper tenantModuleMapper;
    public List<TenantModuleDto> tenantModules(){
        List<TenantModuleDto> tenantModules = tenantModuleMapper.tenantModuleDtoList();
        return tenantModules;
    }
    
    public List<TenantModuleDto> getTenantModules(Integer tenantId) {
        return tenantModuleMapper.getTenantModules(tenantId);
    }
}
