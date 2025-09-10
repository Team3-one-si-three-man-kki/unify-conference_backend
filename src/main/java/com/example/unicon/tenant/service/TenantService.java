package com.example.unicon.tenant.service;

import com.example.unicon.tenant.vo.TenantVO;
import com.example.unicon.user.dto.SignupRequestDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface TenantService {
    /**
     * 서브도메인이 사용 가능한지 확인
     */
    boolean isSubDomainAvailable(String subDomain);

    /**
     * 회원가입 요청 정보를 바탕으로 테넌트를 생성
     *
     * @return 생성된 Tenant 객체 (ID 포함)
     */
    TenantVO createTenant(SignupRequestDTO signupRequest);

    List<TenantVO> findTenants(int page, int size, String keyword);

    long countTenants(String keyword);

    TenantVO findById(Integer tenantId);

    TenantVO create(TenantVO req);

    TenantVO update(TenantVO req);

    void delete(Integer tenantId);
}