package com.example.unicon.domain.sessionModule.service;

import com.example.unicon.domain.sessionModule.dto.SessionModuleCreateRequestDto;
import com.example.unicon.domain.sessionModule.dto.SessionModuleResponseDto;
import com.example.unicon.domain.sessionModule.mapper.SessionModuleMapper;
import com.example.unicon.domain.sessionModule.model.SessionModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SessionModuleService {
    
    private final SessionModuleMapper sessionModuleMapper;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public SessionModuleResponseDto createSessionModule(SessionModuleCreateRequestDto request) {
        try {
            // 배치 정보 생성
            Map<String, Object> positionConfig = new HashMap<>();
            positionConfig.put("area", request.getArea());
            positionConfig.put("position", request.getPosition());
            
            SessionModule sessionModule = new SessionModule();
            sessionModule.setSessionId(request.getSessionId());
            sessionModule.setModuleId(request.getModuleId());
            sessionModule.setSessionModuleConfig(objectMapper.writeValueAsString(positionConfig));
            
            sessionModuleMapper.insertSessionModule(sessionModule);
            
            SessionModuleResponseDto response = new SessionModuleResponseDto();
            response.setSessionId(request.getSessionId());
            response.setModuleId(request.getModuleId());
            response.setArea(request.getArea());
            response.setPosition(request.getPosition());
            response.setSuccess(true);
            response.setMessage("세션 모듈이 성공적으로 생성되었습니다.");
            
            return response;
            
        } catch (JsonProcessingException e) {
            SessionModuleResponseDto response = new SessionModuleResponseDto();
            response.setSuccess(false);
            response.setMessage("세션 모듈 생성 중 오류가 발생했습니다: " + e.getMessage());
            return response;
        }
    }
    
    @Transactional
    public void saveSessionModules(Integer sessionId, Map<String, Object> layoutConfig) {
        if (layoutConfig == null) return;
        
        System.out.println("=== SessionModule 저장 시작 ===");
        System.out.println("Session ID: " + sessionId);
        System.out.println("LayoutConfig: " + layoutConfig);
        
        try {
            // 기존 세션 모듈들 삭제
            sessionModuleMapper.deleteAllBySessionId(sessionId);
            
            // layoutConfig에서 modules 추출
            Object modulesObj = layoutConfig.get("modules");
            System.out.println("Modules Object: " + modulesObj);
            
            if (modulesObj instanceof Map) {
                Map<String, Object> modulesMap = (Map<String, Object>) modulesObj;
                System.out.println("Modules Map: " + modulesMap);
                
                // 각 영역(area)별로 모듈 처리
                for (Map.Entry<String, Object> areaEntry : modulesMap.entrySet()) {
                    String areaName = areaEntry.getKey();
                    System.out.println("Processing area: " + areaName);
                    
                    if (areaEntry.getValue() instanceof List) {
                        List<?> modules = (List<?>) areaEntry.getValue();
                        System.out.println("Area " + areaName + " modules: " + modules);
                        
                        for (int position = 0; position < modules.size(); position++) {
                            Object moduleObj = modules.get(position);
                            System.out.println("Processing module at position " + position + ": " + moduleObj);
                            
                            if (moduleObj instanceof Map) {
                                Map<?, ?> module = (Map<?, ?>) moduleObj;
                                
                                // moduleId 추출
                                Object moduleIdObj = module.get("moduleId");
                                if (moduleIdObj == null) {
                                    moduleIdObj = module.get("id");
                                }
                                System.out.println("Module ID Object: " + moduleIdObj);
                                
                                // 고정 모듈 제외
                                Object isFixed = module.get("isFixed");
                                System.out.println("Is Fixed: " + isFixed);
                                if (isFixed != null && Boolean.TRUE.equals(isFixed)) {
                                    System.out.println("Skipping fixed module: " + moduleIdObj);
                                    continue;
                                }
                                
                                if (moduleIdObj != null) {
                                    Integer moduleId = getModuleId(moduleIdObj);
                                    System.out.println("Converted Module ID: " + moduleId);
                                    
                                    if (moduleId != null) {
                                        SessionModuleCreateRequestDto request = new SessionModuleCreateRequestDto();
                                        request.setSessionId(sessionId);
                                        request.setModuleId(moduleId);
                                        request.setArea(areaName);
                                        request.setPosition(position);
                                        
                                        System.out.println("Creating session module: " + request);
                                        createSessionModule(request);
                                    } else {
                                        System.out.println("Module ID is null after conversion for: " + moduleIdObj);
                                    }
                                } else {
                                    System.out.println("Module ID Object is null for module: " + module);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("세션 모듈 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("세션 모듈 저장 중 오류 발생", e);
        }
        System.out.println("=== SessionModule 저장 완료 ===");
    }
    
    public List<SessionModule> getSessionModules(Integer sessionId) {
        return sessionModuleMapper.findBySessionId(sessionId);
    }
    
    public SessionModule getSessionModule(Integer sessionId, Integer moduleId) {
        return sessionModuleMapper.findBySessionIdAndModuleId(sessionId, moduleId);
    }
    
    @Transactional
    public void deleteSessionModule(Integer sessionId, Integer moduleId) {
        sessionModuleMapper.deleteSessionModule(sessionId, moduleId);
    }
    
    @Transactional
    public void deleteAllSessionModules(Integer sessionId) {
        sessionModuleMapper.deleteAllBySessionId(sessionId);
    }
    
    // 모듈 ID 변환 헬퍼 메서드
    private Integer getModuleId(Object moduleIdObj) {
        try {
            if (moduleIdObj instanceof String) {
                String moduleCode = moduleIdObj.toString();
                return getModuleIdByCode(moduleCode);
            } else {
                return Integer.valueOf(moduleIdObj.toString());
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private Integer getModuleIdByCode(String moduleCode) {
        // 백엔드 로그에서 확인된 실제 모듈 ID 매핑
        switch (moduleCode) {
            case "MIC": return 1;
            case "CAMERA": return 2;
            case "CANVAS": return 3;  // 추가: 캔버스 모듈
            case "QUIZ": return 4;    // 추가: 퀴즈 모듈
            case "FACEAI": return 5;  // 수정: 집중도 체크 AI 모듈
            case "SCREEN": return 8;  // 수정: 화면공유 모듈
            case "ATTENDANCE": return 11; // 추가: 출석 체크 모듈
            case "CHAT": return 4;    // 채팅 모듈 (기존 유지)
            case "WHITEBOARD": return 5; // 화이트보드 모듈 (기존 유지)
            default:
                try {
                    return Integer.valueOf(moduleCode);
                } catch (NumberFormatException e) {
                    System.out.println("Unknown module code: " + moduleCode);
                    return null;
                }
        }
    }
}