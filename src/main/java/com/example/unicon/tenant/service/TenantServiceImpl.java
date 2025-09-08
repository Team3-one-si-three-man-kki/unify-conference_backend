package com.example.unicon.tenant.service;

import com.example.unicon.tenant.mapper.TenantMapper;
import com.example.unicon.tenant.vo.TenantVO;
import com.example.unicon.user.dto.SignupRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

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


    @Override
    public List<TenantVO> findTenants(int page, int size, String keyword) {
        int p = Math.max(1, page);
        int s = Math.min(Math.max(1, size), 200);
        int offset = (p - 1) * s;
        String kw = (StringUtils.hasText(keyword) ? keyword.trim() : null);
        return tenantMapper.findTenants(offset, s, kw);
    }

    @Override
    public long countTenants(String keyword) {
        String kw = (StringUtils.hasText(keyword) ? keyword.trim() : null);
        return tenantMapper.countTenants(kw);
    }

    @Override
    public TenantVO findById(Integer tenantId) {
        return tenantMapper.findById(tenantId);
    }

    @Override
    public TenantVO create(TenantVO req) {
        if (req.getIsActive() == null) req.setIsActive(Boolean.TRUE);
        tenantMapper.insert(req);
        return tenantMapper.findById(req.getTenantId());
    }

    @Override
    public TenantVO update(TenantVO req) {
        tenantMapper.update(req);
        return tenantMapper.findById(req.getTenantId());
    }

    @Override
    public void delete(Integer tenantId) {
        tenantMapper.delete(tenantId);
    }

}
