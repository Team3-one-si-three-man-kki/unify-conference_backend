package com.example.unicon.module.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 자동으로 만듭니다.
public class ModuleUsageDto {
    private final String roomName;
    private final String lastUsed;
}