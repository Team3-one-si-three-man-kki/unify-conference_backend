package com.example.unicon.domain.tenantModule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantModuleDto {
    private int tenantId;
    private int moduleId;
    private String code;
    private String name;
    private String description;
    private String icon;
}
