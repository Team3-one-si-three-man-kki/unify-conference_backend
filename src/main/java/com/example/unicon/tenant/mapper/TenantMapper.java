package com.example.unicon.tenant.mapper;

import com.example.unicon.tenant.vo.TenantVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface TenantMapper {
    /**
     * 서브도메인으로 테넌트 존재 여부 확인
     */
    Optional<TenantVO> findBySubDomain(String subDomain);

    /**
     * 테넌트 정보 저장
     */
    void insertTenant(TenantVO tenant);
}
