package com.example.unicon.domain.sessionModule.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionModule {
    private Integer sessionId; // FK to session.session_id
    private Integer moduleId;  // FK to module.module_id
    private String sessionModuleConfig; // JSON string for module config/position
}