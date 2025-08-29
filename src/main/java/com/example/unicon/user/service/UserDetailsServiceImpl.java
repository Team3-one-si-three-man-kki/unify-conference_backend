package com.example.unicon.user.service;

import com.example.unicon.user.mapper.UserMapper;
import com.example.unicon.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // 이 어노테이션이 있어야 Spring이 이 클래스를 Bean으로 인식합니다.
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // email을 사용하여 사용자 정보를 데이터베이스에서 조회합니다.
        return userMapper.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }

    private UserDetails createUserDetails(UserVO userVO) {
        // UserDetails 객체를 생성하여 Spring Security에 반환합니다.
        return User.builder()
                .username(userVO.getEmail()) // Principal이 될 고유 식별자
                .password(userVO.getPassword())
                .roles(userVO.getRole()) // "ROLE_" 접두사는 Spring Security가 자동으로 추가
                .build();
    }
}