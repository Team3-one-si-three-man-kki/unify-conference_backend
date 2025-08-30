package com.example.unicon.module.service;

import com.example.unicon.module.dto.ModuleDetailDto;
import com.example.unicon.module.dto.ModuleUsageDto;

import java.util.List;

public interface ModuleService {
    List<ModuleDetailDto> getTenantModules(Integer tenantId);
    List<ModuleUsageDto> getModuleUsage(String moduleId, Integer tenantId);
}