package com.example.unicon.domain.sessionModule.controller;

import com.example.unicon.domain.sessionModule.dto.SessionModuleCreateRequestDto;
import com.example.unicon.domain.sessionModule.dto.SessionModuleResponseDto;
import com.example.unicon.domain.sessionModule.model.SessionModule;
import com.example.unicon.domain.sessionModule.service.SessionModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/session-module")
@RequiredArgsConstructor
public class SessionModuleController {
    
    private final SessionModuleService sessionModuleService;
    
    // 세션 모듈 생성
    @PostMapping
    public ResponseEntity<SessionModuleResponseDto> createSessionModule(
            @RequestBody SessionModuleCreateRequestDto request) {
        
        SessionModuleResponseDto response = sessionModuleService.createSessionModule(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 세션의 모든 모듈 조회
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<SessionModule>> getSessionModules(@PathVariable Integer sessionId) {
        List<SessionModule> modules = sessionModuleService.getSessionModules(sessionId);
        return ResponseEntity.ok(modules);
    }
    
    // 특정 세션-모듈 조회
    @GetMapping("/session/{sessionId}/module/{moduleId}")
    public ResponseEntity<SessionModule> getSessionModule(
            @PathVariable Integer sessionId, 
            @PathVariable Integer moduleId) {
        
        SessionModule sessionModule = sessionModuleService.getSessionModule(sessionId, moduleId);
        
        if (sessionModule != null) {
            return ResponseEntity.ok(sessionModule);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 특정 세션-모듈 삭제
    @DeleteMapping("/session/{sessionId}/module/{moduleId}")
    public ResponseEntity<Void> deleteSessionModule(
            @PathVariable Integer sessionId, 
            @PathVariable Integer moduleId) {
        
        sessionModuleService.deleteSessionModule(sessionId, moduleId);
        return ResponseEntity.ok().build();
    }
    
    // 세션의 모든 모듈 삭제
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> deleteAllSessionModules(@PathVariable Integer sessionId) {
        sessionModuleService.deleteAllSessionModules(sessionId);
        return ResponseEntity.ok().build();
    }
}