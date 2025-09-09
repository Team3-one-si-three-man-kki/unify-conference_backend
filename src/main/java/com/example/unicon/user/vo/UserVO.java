package com.example.unicon.user.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserVO {

    private Integer id;
    private Integer tenantId;
    private String userName; // DTO와 로직에서 사용하는 필드명
    private String email;
    private String password;
    private String role;
    private String rowStatus; // CUD 처리용
    private String userId;
    private String createAt;
    private String searchKeyword;

    // JOIN 시 tenant 테이블에서 값을 받아올 필드들
    private boolean isActive;
    private String tenantName;
    private String subDomain;

    @Builder
    public UserVO(Integer id, Integer tenantId, String userName, String email, String password, String role, boolean isActive, String tenantName, String subDomain) {
        this.id = id;
        this.tenantId = tenantId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
        this.tenantName = tenantName;
        this.subDomain = subDomain;
    }
}