package com.example.unicon.tenant.service;

import com.example.unicon.tenant.mapper.TenantMapper;
import com.example.unicon.tenant.vo.TenantVO;
import com.example.unicon.user.dto.SignupRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {
    private final TenantMapper tenantMapper;

    @Override
    public boolean isSubDomainAvailable(String subDomain) {
        return tenantMapper.findBySubDomain(subDomain).isEmpty();
    }

    @Override
    public TenantVO createTenant(SignupRequestDTO signupRequest) {
        if (!isSubDomainAvailable(signupRequest.subDomain())) {
            throw new IllegalArgumentException("이미 사용 중인 서브도메인입니다: " + signupRequest.subDomain());
        }

        TenantVO tenant = TenantVO.builder()
                .name(signupRequest.tenantName())
                .subDomain(signupRequest.subDomain())
                .isActive(true)
                .build();

        tenantMapper.insertTenant(tenant);
        return tenant;
    }

}
