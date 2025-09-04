package com.example.unicon.domain.session.service;

import com.example.unicon.domain.session.dto.SessionCreateRequestDto;
import com.example.unicon.domain.session.dto.SessionCreateResponseDto;
import com.example.unicon.domain.session.model.Session;
import com.example.unicon.domain.session.mapper.SessionMapper;
import com.example.unicon.domain.sessionModule.service.SessionModuleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SessionService {
    
    private final SessionMapper sessionMapper;
    private final SessionModuleService sessionModuleService;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public SessionCreateResponseDto createSession(SessionCreateRequestDto request, Integer tenantId, Integer userId) {
        try {
            // 세션 엔티티 생성
            Session session = new Session();
            session.setName(request.getName());
            session.setStartTime(parseDateTime(request.getStartTime()));
            session.setEndTime(parseDateTime(request.getEndTime()));
            session.setMaxParticipant(request.getMaxParticipants());
            // inviteLink는 세션 ID 생성 후 설정
            // 링크 만료 시간을 세션 종료 시간 + 1시간으로 설정
            LocalDateTime sessionEndTime = parseDateTime(request.getEndTime());
            LocalDateTime linkExpiry = (sessionEndTime != null) ? sessionEndTime.plusHours(1) : LocalDateTime.now().plusDays(7);
            session.setLinkExpiry(linkExpiry);
            session.setCreatedBy(userId);
            session.setTenantId(tenantId);
            
            // 세션 저장 (auto-generated sessionId 받아옴)
            sessionMapper.insertSession(session);
            
            // 생성된 세션 ID로 초대 링크 생성
            Integer sessionId = session.getSessionId();
            String inviteLink = "http://localhost:5174/session/" + sessionId;
            session.setInviteLink(inviteLink);
            
            // 초대 링크 정보를 데이터베이스에 업데이트
            sessionMapper.updateSession(session);
            
            // 세션-모듈 관계 저장 (SessionModule 도메인 사용)
            sessionModuleService.saveSessionModules(sessionId, request.getLayoutConfig());
            
            // 응답 생성
            SessionCreateResponseDto response = new SessionCreateResponseDto();
            response.setSessionId(sessionId.toString());
            response.setInviteLink(inviteLink);
            response.setSuccess(true);
            response.setMessage("세션이 성공적으로 생성되었습니다.");
            
            return response;
            
        } catch (Exception e) {
            SessionCreateResponseDto response = new SessionCreateResponseDto();
            response.setSuccess(false);
            response.setMessage("세션 생성 중 오류가 발생했습니다: " + e.getMessage());
            return response;
        }
    }
    
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null) return null;
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }
}