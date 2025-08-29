package com.example.unicon.tenant.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter // MyBatis가 ID를 주입할 수 있도록 Setter 추가
@Builder
public class TenantVO{
    private int tenantId; // DB의 tenant_id와 매핑
    private String name;
    private String subDomain;
    private boolean isActive;
    private LocalDateTime createdAt;
}
