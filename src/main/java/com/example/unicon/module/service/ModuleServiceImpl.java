package com.example.unicon.module.service;

import com.example.unicon.module.dto.ModuleDetailDto;
import com.example.unicon.module.dto.ModuleUsageDto;
import com.example.unicon.module.mapper.ModuleMapper;
import com.example.unicon.module.vo.ModuleUsageVO;
import com.example.unicon.module.vo.TenantModuleDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    private final ModuleMapper moduleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ModuleDetailDto> getTenantModules(Integer tenantId) {
        List<TenantModuleDetailVO> moduleDetails = moduleMapper.findModulesByTenantId(tenantId);
        return moduleDetails.stream()
                .map(ModuleDetailDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    // 👇 [수정] tenantId 파라미터 추가
    public List<ModuleUsageDto> getModuleUsage(String moduleId, Integer tenantId) {
        // 👇 [수정] Mapper 호출 시 tenantId 전달
        List<ModuleUsageVO> usageVOs = moduleMapper.findUsageByModuleId(moduleId, tenantId);

        return usageVOs.stream()
                .map(vo -> new ModuleUsageDto(vo.getName(), vo.getStartTime()))
                .collect(Collectors.toList());
    }
}