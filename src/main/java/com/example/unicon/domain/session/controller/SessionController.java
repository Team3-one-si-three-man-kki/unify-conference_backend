package com.example.unicon.domain.session.controller;

import com.example.unicon.domain.session.dto.SessionCreateRequestDto;
import com.example.unicon.domain.session.dto.SessionCreateResponseDto;
import com.example.unicon.domain.session.model.Session;
import com.example.unicon.domain.session.service.SessionService;
import com.example.unicon.user.mapper.UserMapper;
import com.example.unicon.user.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/session")
@RequiredArgsConstructor
public class SessionController {
    
    private final SessionService sessionService;
    private final UserMapper userMapper;
    
    @PostMapping
    public ResponseEntity<SessionCreateResponseDto> createSession(
            @RequestBody SessionCreateRequestDto request,
            HttpServletRequest httpRequest) {
        
        // JWT에서 tenantId와 email 추출
        String tenantIdStr = (String) httpRequest.getAttribute("tenantId");
        String email = (String) httpRequest.getAttribute("email");
        
        if (tenantIdStr == null || email == null) {
            SessionCreateResponseDto response = new SessionCreateResponseDto();
            response.setSuccess(false);
            response.setMessage("인증 정보가 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // email로 사용자 정보 조회하여 userId 획득
            Optional<UserVO> userOptional = userMapper.findByEmail(email);
            if (userOptional.isEmpty()) {
                SessionCreateResponseDto response = new SessionCreateResponseDto();
                response.setSuccess(false);
                response.setMessage("사용자 정보를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            UserVO currentUser = userOptional.get();
            Integer tenantId = Integer.valueOf(tenantIdStr);
            Integer userId = currentUser.getId();
            
            SessionCreateResponseDto response = sessionService.createSession(request, tenantId, userId);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            SessionCreateResponseDto response = new SessionCreateResponseDto();
            response.setSuccess(false);
            response.setMessage("사용자 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 테넌트별 세션 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<List<Session>> getSessionsByTenant(HttpServletRequest httpRequest) {
        // JWT에서 tenantId 추출
        String tenantIdStr = (String) httpRequest.getAttribute("tenantId");
        
        if (tenantIdStr == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Integer tenantId = Integer.valueOf(tenantIdStr);
            List<Session> sessions = sessionService.getSessionsByTenant(tenantId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}