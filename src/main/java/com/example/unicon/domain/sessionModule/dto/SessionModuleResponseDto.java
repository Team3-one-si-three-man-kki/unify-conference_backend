package com.example.unicon.domain.sessionModule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionModuleResponseDto {
    private Integer sessionId;
    private Integer moduleId;
    private String area;
    private Integer position;
    private boolean success;
    private String message;
}