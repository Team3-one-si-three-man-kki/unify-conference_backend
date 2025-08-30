package com.example.unicon.user.service;

import com.example.unicon.tenant.service.TenantService;
import com.example.unicon.tenant.vo.TenantVO;
import com.example.unicon.user.dao.UserDAO;
import com.example.unicon.user.dto.LoginRequestDTO;
import com.example.unicon.user.dto.SignupRequestDTO;
import com.example.unicon.user.dto.UserResponseDTO;
import com.example.unicon.user.mapper.UserMapper;
import com.example.unicon.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TenantService tenantService;
    private final UserMapper userMapper;
    private final UserDAO userDAO;
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

    @Override
    @Transactional(readOnly = true)
    public Optional<UserVO> getUserByEmailAndSubdomain(String email, String subDomain) {
        // 일반 로그인 시 사용했던 매퍼 메소드를 재사용하여 사용자를 조회합니다.
        return userMapper.findUserWithTenantByEmailAndSubdomain(email, subDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVO> selectUsersByTenant(UserVO vo) throws Exception {
        return userDAO.selectUsersByTenant(vo);
    }

    @Override
    @Transactional
    public void saveUserList(List<UserVO> userList) throws Exception {
        for (UserVO user : userList) {
            String rowStatus = user.getRowStatus();

            if ("C".equals(rowStatus)) { // Create (Insert)
                // 비밀번호 암호화
                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                }
                insertUser(user);

            } else if ("U".equals(rowStatus)) { // Update
                // 비밀번호가 변경된 경우에만 암호화
                if (user.getPassword() != null && !user.getPassword().isEmpty() && !"******".equals(user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                } else {
                    // 기존 비밀번호 유지를 위해 null로 설정
                    user.setPassword(null);
                }
                updateUser(user);

            } else if ("D".equals(rowStatus)) { // Delete
                deleteUser(user);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailableInTenant(String email, String tenantId) throws Exception {
        UserVO searchVo = new UserVO();
        searchVo.setEmail(email);
        searchVo.setTenantId(Integer.valueOf(tenantId));

        List<UserVO> existingUsers = userDAO.selectUsersByEmailAndTenant(searchVo);
        return existingUsers.isEmpty();
    }

    @Override
    @Transactional
    public int insertUser(UserVO userVo) throws Exception {
        return userDAO.insertUser(userVo);
    }

    @Override
    @Transactional
    public int updateUser(UserVO userVo) throws Exception {
        return userDAO.updateUser(userVo);
    }

    @Override
    @Transactional
    public int deleteUser(UserVO userVo) throws Exception {
        return userDAO.deleteUser(userVo);
    }

    @Override
    @Transactional(readOnly = true)
    public UserVO selectUser(UserVO userVo) throws Exception {
        return userDAO.selectUser(userVo);
    }
}