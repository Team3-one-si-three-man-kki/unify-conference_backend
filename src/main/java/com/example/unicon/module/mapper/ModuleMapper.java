package com.example.unicon.module.mapper;

import com.example.unicon.module.vo.ModuleUsageVO;
import com.example.unicon.module.vo.TenantModuleDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ModuleMapper {
    /**
     * 특정 테넌트가 보유한 모든 모듈의 상세 정보를 조회합니다.
     * @param tenantId 테넌트 ID
     * @return 모듈 상세 정보 목록
     */
    List<TenantModuleDetailVO> findModulesByTenantId(@Param("tenantId") Integer tenantId);

    List<ModuleUsageVO> findUsageByModuleId(@Param("moduleId") String moduleId, @Param("tenantId") Integer tenantId);
}