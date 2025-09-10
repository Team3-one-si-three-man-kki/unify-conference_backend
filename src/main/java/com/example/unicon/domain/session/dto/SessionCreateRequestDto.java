package com.example.unicon.domain.session.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionCreateRequestDto {
    private String name;
    private String startTime;
    private String endTime;
    private Integer maxParticipants;
    private Map<String, Object> layoutConfig;
    private List<String> inviteEmails;
}