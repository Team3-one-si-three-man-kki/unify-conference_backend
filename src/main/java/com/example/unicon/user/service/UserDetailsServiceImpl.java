package com.example.unicon.user.service;

import com.example.unicon.user.mapper.UserMapper;
import com.example.unicon.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service // 이 어노테이션이 있어야 Spring이 이 클래스를 Bean으로 인식합니다.
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserVO user = userMapper.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 👇 "ROLE_" 접두사를 붙여서 "ROLE_MANAGER"와 같은 형태로 권한을 생성합니다.
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
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