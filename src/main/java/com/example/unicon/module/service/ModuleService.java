package com.example.unicon.module.service;

import com.example.unicon.module.dto.ModuleDetailDto;
import com.example.unicon.module.dto.ModuleUsageDto;
import com.example.unicon.module.vo.ModuleVO;
import com.example.unicon.module.vo.TenantModuleVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ModuleService {
    List<ModuleDetailDto> getTenantModules(Integer tenantId);
    List<ModuleUsageDto> getModuleUsage(String moduleId, Integer tenantId);

    //모듈 마켓플레이스
    List<ModuleVO> selectListModule(ModuleVO moduleVo) throws Exception;
    long selectListCountModule(ModuleVO moduleVo);
    ModuleVO selectModule(ModuleVO moduleVo);
    int subscribeModule(TenantModuleVO tenantModuleVo) throws Exception;
    int unsubscribeModule(TenantModuleVO tenantModuleVo) throws Exception;
    List<TenantModuleVO> selectSubscribedModules(TenantModuleVO tenantModuleVo) throws Exception;
    boolean isModuleSubscribed(TenantModuleVO tenantModuleVo) throws Exception;
    boolean isDuplicateSubscription(TenantModuleVO tenantModuleVo) throws Exception;

    int insertModule(ModuleVO moduleVo) throws Exception;
    int updateModule(ModuleVO moduleVo) throws Exception;
    int deleteModule(ModuleVO moduleVo) throws Exception;


}