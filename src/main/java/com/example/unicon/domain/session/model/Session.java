package com.example.unicon.domain.session.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    private Integer sessionId; // int auto_increment
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String inviteLink;
    private LocalDateTime linkExpiry;
    private Integer createdBy;
    private Integer tenantId;
    private Integer maxParticipant; // 기존 테이블명 맞춤
}