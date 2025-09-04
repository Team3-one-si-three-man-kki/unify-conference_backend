package com.example.unicon.domain.tenantModule.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantModule {
    private int tenantId;
    private int moduleId;
    private LocalDateTime purchasedAt;
}
