package com.example.unicon.user.service;

import com.example.unicon.tenant.service.TenantService;
import com.example.unicon.tenant.vo.TenantVO;
import com.example.unicon.user.dto.LoginRequestDTO;
import com.example.unicon.user.dto.SignupRequestDTO;
import com.example.unicon.user.dto.UserResponseDTO;
import com.example.unicon.user.mapper.UserMapper;
import com.example.unicon.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TenantService tenantService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean isEmailAvailable(String email) {
        return userMapper.findByEmail(email).isEmpty();
    }

    @Override
    @Transactional
    public UserResponseDTO processSignup(SignupRequestDTO signupRequest) {
        // ... (이전 코드와 동일)
        if (!"kakao".equalsIgnoreCase(signupRequest.signupType())) {
            if (signupRequest.password() == null || !signupRequest.password().equals(signupRequest.passwordConfirm())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        }

        TenantVO newTenant = tenantService.createTenant(signupRequest);
        UserVO newUser = createUser(signupRequest, newTenant.getTenantId());

        return new UserResponseDTO(newUser, newTenant.getName(), newTenant.getSubDomain());
    }

    @Override
    @Transactional(readOnly = true)
    public UserVO processLogin(LoginRequestDTO loginRequest) {
        // 1. 사용자 정보 조회
        UserVO user = userMapper.findUserWithTenantByEmailAndSubdomain(loginRequest.email(), loginRequest.subDomain())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 인증된 사용자 정보 반환
        return user;
    }

    private UserVO createUser(SignupRequestDTO signupRequest, Integer tenantId) {
        if (!isEmailAvailable(signupRequest.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + signupRequest.email());
        }

        String encryptedPassword = null;
        if (!"kakao".equalsIgnoreCase(signupRequest.signupType())) {
            encryptedPassword = passwordEncoder.encode(signupRequest.password());
        }

        UserVO user = UserVO.builder()
                .tenantId(tenantId)
                .userName(signupRequest.userName()) // 필드명 확인
                .email(signupRequest.email())
                .password(encryptedPassword)
                .role("manager")
                .build();

        userMapper.insertUser(user);
        return user;
    }
}