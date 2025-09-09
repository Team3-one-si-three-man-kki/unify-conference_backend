package com.example.unicon.user.controller;

import com.example.unicon.infrastructure.redis.token.RefreshTokenRepository;
import com.example.unicon.infrastructure.redis.token.TokenBlacklistRepository;
import com.example.unicon.user.dto.EmailCheckRequestDTO;
import com.example.unicon.user.dto.LoginRequestDTO;
import com.example.unicon.user.dto.SignupRequestDTO;
import com.example.unicon.user.dto.UserResponseDTO;
import com.example.unicon.user.service.UserService;
import com.example.unicon.user.vo.UserVO;
import com.example.unicon.util.CookieUtil;
import com.example.unicon.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieUtil cookieUtil;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @PostMapping("/guest/verify-recaptcha")
    public ResponseEntity<Map<String, Object>> verifyRecaptcha(@RequestBody Map<String, String> payload) {
        String recaptchaToken = payload.get("recaptchaToken");
        boolean isVerified = userService.verifyRecaptcha(recaptchaToken);

        if (isVerified) {
            return ResponseEntity.ok(Map.of("success", true, "message", "reCAPTCHA verification successful."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "reCAPTCHA verification failed."));
        }
    }

    // ... signup 메소드는 동일 ...
    @PostMapping("/guest/signup")
    public ResponseEntity<UserResponseDTO> signup(@Valid @RequestBody SignupRequestDTO signupRequest) {
        UserResponseDTO response = userService.processSignup(signupRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/guest/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        UserVO user = userService.processLogin(loginRequest);

        // 2. 토큰 생성 시 userId 대신 email 사용
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getEmail(), // userId -> email
                user.getTenantId().toString(),
                user.getRole(),
                user.isActive()
        );
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail()); // userId -> email

        // 3. Refresh Token 저장 (Redis Key를 email로 사용)
        refreshTokenRepository.save(
                user.getEmail(), // userId -> email
                refreshToken,
                jwtTokenProvider.getRefreshTokenExpiration()
        );

        // 4. 응답 설정 (이전과 동일)
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        long refreshTokenExpirationSeconds = jwtTokenProvider.getRefreshTokenExpiration() / 1000;
        cookieUtil.setRefreshTokenCookie(response, refreshToken, refreshTokenExpirationSeconds);

        // 5. 응답 본문으로 사용자 정보 반환 (이전과 동일)
        UserResponseDTO responseDTO = new UserResponseDTO(user, user.getTenantName(), user.getSubDomain());
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/guest/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@Valid @RequestBody EmailCheckRequestDTO requestDTO) {
        boolean isAvailable = userService.isEmailAvailable(requestDTO.email());
        if (isAvailable) {
            return ResponseEntity.ok(Map.of("isAvailable", true, "message", "사용 가능한 이메일입니다."));
        } else {
            // 409 Conflict 상태 코드는 "이미 리소스가 존재하여 충돌이 발생했다"는 의미로,
            // 중복된 이메일 응답에 적합합니다.
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("isAvailable", false, "message", "이미 사용 중인 이메일입니다."));
        }
    }
    @PostMapping("/user/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. Authorization 헤더에서 Access Token 추출
        String accessToken = jwtTokenProvider.resolveToken(request);

        if (accessToken != null) {
            // 2. Access Token 남은 유효시간 계산하여 Redis 블랙리스트에 추가
            long remainingTime = jwtTokenProvider.getTokenRemainingTime(accessToken);
            if (remainingTime > 0) {
                tokenBlacklistRepository.add(accessToken, remainingTime);
            }
        }

        // 3. SecurityContext에서 사용자 정보(email) 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            // 4. Redis에 저장된 Refresh Token 삭제
            refreshTokenRepository.delete(authentication.getName());
        }

        // 5. 클라이언트의 Refresh Token 쿠키 삭제
        cookieUtil.clearRefreshTokenCookie(response);

        return ResponseEntity.ok(Map.of("message", "성공적으로 로그아웃되었습니다."));
    }

    /**
     * 테넌트별 사용자 목록 조회
     */
    @GetMapping("/user/list")
    public ResponseEntity<Map<String, Object>> getUserList(
            @RequestParam Integer tenantId,
            @RequestParam(required = false) String searchKeyword) {

        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("사용자 목록 조회 요청 - tenantId: " + tenantId + ", searchKeyword: " + searchKeyword);

            UserVO vo = new UserVO();
            vo.setTenantId(tenantId);
            vo.setSearchKeyword(searchKeyword);

            List<UserVO> userList = userService.selectUsersByTenant(vo);

            response.put("success", true);
            response.put("data", userList);
            response.put("total", userList.size());

            System.out.println("조회 결과 개수: " + userList.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("사용자 목록 조회 중 오류: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "사용자 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            response.put("data", new ArrayList<>());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 사용자 목록 저장 (CUD 처리)
     */
    @PostMapping("/user/save")
    public ResponseEntity<Map<String, Object>> saveUsers(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("사용자 저장 요청 받음: " + requestData);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> saveDataList = (List<Map<String, Object>>) requestData.get("saveDataList");

            if (saveDataList != null && !saveDataList.isEmpty()) {
                List<UserVO> userList = new ArrayList<>();

                for (Map<String, Object> userData : saveDataList) {
                    UserVO userVo = new UserVO();

                    // 데이터 매핑
                    Object userIdObj = userData.get("userId");
                    String userId = (userIdObj != null) ? String.valueOf(userIdObj) : null;
                    String rowStatus = (String) userData.get("rowStatus");

                    userVo.setUserId(userId == null || userId.isEmpty() || "null".equals(userId) ? null : userId);

                    // tenantId 처리 개선
                    Object tenantIdObj = userData.get("tenantId");
                    if (tenantIdObj instanceof Integer) {
                        userVo.setTenantId((Integer) tenantIdObj);
                    } else if (tenantIdObj instanceof String) {
                        userVo.setTenantId(Integer.valueOf((String) tenantIdObj));
                    }

                    userVo.setUserName((String) userData.get("name"));
                    userVo.setEmail((String) userData.get("email"));

                    // 비밀번호 처리
                    String password = (String) userData.get("password");
                    if (password != null && !password.isEmpty() && !"******".equals(password)) {
                        userVo.setPassword(password);
                    }

                    userVo.setRole((String) userData.get("role"));
                    userVo.setCreateAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    userVo.setRowStatus(rowStatus != null ? rowStatus : "C"); // 기본값 설정

                    // isActive 처리
                    Object isActiveObj = userData.get("isActive");
                    if (isActiveObj instanceof Boolean) {
                        userVo.setActive((Boolean) isActiveObj);
                    } else {
                        userVo.setActive(true);
                    }

                    userList.add(userVo);
                }

                // 서비스 호출
                userService.saveUserList(userList);

                response.put("success", true);
                response.put("message", "저장이 완료되었습니다.");
                System.out.println("사용자 저장 완료");
            } else {
                response.put("success", false);
                response.put("message", "저장할 데이터가 없습니다.");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("사용자 저장 중 오류: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "저장 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 테넌트별 이메일 중복 검사
     */
    @PostMapping("/user/check-email-by-tenant")
    public ResponseEntity<Map<String, Object>> checkEmailByTenant(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = (String) requestData.get("email");
            Object tenantIdObj = requestData.get("tenantId");

            Integer tenantId;
            if (tenantIdObj instanceof Integer) {
                tenantId = (Integer) tenantIdObj;
            } else if (tenantIdObj instanceof String) {
                tenantId = Integer.valueOf((String) tenantIdObj);
            } else {
                throw new IllegalArgumentException("잘못된 tenantId 형식입니다.");
            }

            System.out.println("이메일 중복 검사 요청 - email: " + email + ", tenantId: " + tenantId);

            boolean available = userService.isEmailAvailableInTenant(email, String.valueOf(tenantId));

            response.put("success", true);
            response.put("available", available);
            response.put("email", email);
            response.put("tenantId", tenantId);

            System.out.println("이메일 중복 검사 결과: " + (available ? "사용가능" : "중복"));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("이메일 중복 검사 중 오류: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "이메일 중복 검사 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 사용자 삭제
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            UserVO userVo = new UserVO();
            userVo.setUserId(userId);

            int result = userService.deleteUser(userVo);

            if (result > 0) {
                response.put("success", true);
                response.put("message", "사용자가 삭제되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "삭제할 사용자를 찾을 수 없습니다.");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("사용자 삭제 중 오류: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "사용자 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}