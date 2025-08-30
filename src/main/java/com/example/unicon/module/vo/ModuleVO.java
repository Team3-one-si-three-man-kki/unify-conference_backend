package com.example.unicon.module.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleVO {
    private String moduleId;
    private String code;
    private String name;
    private String description;
    private String icon;
    private Long price;
}