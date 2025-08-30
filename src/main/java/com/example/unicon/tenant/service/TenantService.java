package com.example.unicon.tenant.service;

import com.example.unicon.tenant.vo.TenantVO;
import com.example.unicon.user.dto.SignupRequestDTO;

public interface TenantService {
    /**
     * 서브도메인이 사용 가능한지 확인
     */
    boolean isSubDomainAvailable(String subDomain);

    /**
     * 회원가입 요청 정보를 바탕으로 테넌트를 생성
     * @return 생성된 Tenant 객체 (ID 포함)
     */
    TenantVO createTenant(SignupRequestDTO signupRequest);
}