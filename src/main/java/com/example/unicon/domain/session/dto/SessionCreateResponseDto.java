package com.example.unicon.domain.session.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionCreateResponseDto {
    private String sessionId;
    private String inviteLink;
    private boolean success;
    private String message;
}