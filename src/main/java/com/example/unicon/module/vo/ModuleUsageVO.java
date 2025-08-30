package com.example.unicon.module.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleUsageVO {
    private String name;        // 세션(미팅룸) 이름
    private String startTime;   // 세션 시작 시간 (마지막 사용일)
}