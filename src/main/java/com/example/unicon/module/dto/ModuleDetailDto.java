package com.example.unicon.module.dto;

import com.example.unicon.module.vo.TenantModuleDetailVO;
import lombok.Getter;

@Getter
public class ModuleDetailDto {
    private final String moduleId;
    private final String name;
    private final String description;
    private final String icon;
    private final String code;
    private final String purchasedAt;

    public ModuleDetailDto(TenantModuleDetailVO vo) {
        this.moduleId = vo.getModuleId();
        this.name = vo.getName();
        this.description = vo.getDescription();
        this.icon = vo.getIcon();
        this.code = vo.getCode();
        this.purchasedAt = vo.getPurchasedAt();
    }
}