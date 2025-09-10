package com.example.unicon.domain.tenantModule.mapper;

import com.example.unicon.domain.tenantModule.dto.TenantModuleDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TenantModuleMapper {
    List<TenantModuleDto> tenantModuleDtoList();
    List<TenantModuleDto> getTenantModules(Integer tenantId);
}
