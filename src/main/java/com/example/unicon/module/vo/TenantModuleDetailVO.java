package com.example.unicon.module.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantModuleDetailVO {
    private String moduleId;
    private String name;
    private String description;
    private String icon;
    private String code;
    private String purchasedAt; // 구매일
}