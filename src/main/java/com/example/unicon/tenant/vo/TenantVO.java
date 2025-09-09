package com.example.unicon.tenant.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter // MyBatis가 ID를 주입할 수 있도록 Setter 추가
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantVO {
    private int tenantId; // DB의 tenant_id와 매핑
    private String name;
    private String subDomain;
    private Boolean isActive;
    private LocalDateTime createdAt;

    private int userCount;
    private int moduleCount;
}
