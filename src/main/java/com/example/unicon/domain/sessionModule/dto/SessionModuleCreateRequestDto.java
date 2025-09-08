package com.example.unicon.domain.sessionModule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionModuleCreateRequestDto {
    private Integer sessionId;
    private Integer moduleId;
    private String area;      // 배치 영역
    private Integer position; // 영역 내 위치
}