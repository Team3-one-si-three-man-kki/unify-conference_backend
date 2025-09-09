package com.example.unicon.module.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantModuleVO {
    private String moduleId;
    private String tenantId;
    private String purchasedAt;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}