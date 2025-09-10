package com.example.unicon.module.mapper;

import com.example.unicon.module.vo.ModuleUsageVO;
import com.example.unicon.module.vo.ModuleVO;
import com.example.unicon.module.vo.TenantModuleDetailVO;
import com.example.unicon.module.vo.TenantModuleVO;
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
    
    /* 모듈 마켓플레이스*/
    // 모듈 목록 조회
    List<ModuleVO> selectListModule(ModuleVO moduleVO);

    // 모듈 목록 전체 카운트
    long selectListCountModule(ModuleVO moduleVO);

    // 모듈 상세 조회
    ModuleVO selectModule(ModuleVO moduleVO);

    // 모듈 등록
    int insertModule(ModuleVO moduleVO);

    // 모듈 수정
    int updateModule(ModuleVO moduleVO);

    // 모듈 삭제
    int deleteModule(ModuleVO moduleVO);

    // 테넌트 모듈 구독 정보 등록
    int insertTenantModule(TenantModuleVO tenantModuleVO);

    // 테넌트 모듈 구독 정보 삭제
    int deleteTenantModule(TenantModuleVO tenantModuleVO);

    // 테넌트의 구독 모듈 목록 조회
    List<TenantModuleVO> selectTenantModules(TenantModuleVO tenantModuleVO);

    // 특정 테넌트-모듈 구독 정보 조회
    TenantModuleVO selectTenantModule(TenantModuleVO tenantModuleVO);

}