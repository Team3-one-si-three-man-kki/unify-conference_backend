package com.example.unicon.tenant.mapper;

import com.example.unicon.tenant.vo.TenantVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
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

    // 목록 + 페이징(키워드 검색: name, sub_domain)
    List<TenantVO> findTenants(@Param("offset") int offset,
                               @Param("size") int size,
                               @Param("keyword") String keyword);

    long countTenants(@Param("keyword") String keyword);

    // 단건
    TenantVO findById(@Param("tenantId") int tenantId);

    // CUD
    int insert(TenantVO vo);   // useGeneratedKeys

    int update(TenantVO vo);

    int delete(@Param("tenantId") int tenantId);
}
