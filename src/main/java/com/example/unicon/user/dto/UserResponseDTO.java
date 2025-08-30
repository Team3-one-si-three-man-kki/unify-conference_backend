package com.example.unicon.user.dto;

import com.example.unicon.user.vo.UserVO;
import lombok.Getter;

@Getter
public class UserResponseDTO {
    private final String userName;
    private final String email;
    private final String role;
    private final String tenantName;
    private final String subDomain;

    public UserResponseDTO(UserVO user, String tenantName, String subDomain) {
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.tenantName = tenantName;
        this.subDomain = subDomain;
    }
}