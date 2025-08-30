package com.example.unicon.user.vo;

/**
 * 사용자 정보를 담는 VO 클래스
 */
public class UserVo {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String tenantId;
    private String name;
    private String email;
    private String password;
    private String role;
    private String createdAt;
    private String updatedAt;
    private boolean isActive = true;
    private String rowStatus; // C: 생성, U: 수정, D: 삭제
    private String searchKeyword;
    private String subDomain;

    // 기본 생성자
    public UserVo() {
    }

    // Getter/Setter 메소드들
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getRowStatus() {
        return rowStatus;
    }

    public void setRowStatus(String rowStatus) {
        this.rowStatus = rowStatus;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "userId='" + userId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                ", rowStatus='" + rowStatus + '\'' +
                '}';
    }
}