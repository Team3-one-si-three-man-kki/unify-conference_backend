package com.example.unicon.attendance.vo;

import java.io.Serializable;

public class AttendanceVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 기본 필드들
    private String recordId;
    private String sessionId;
    private String name;
    private String email;
    private String ipAddress;
    private String joinTime;
    private String leaveTime;
    private String participationMinutes;
    private String status;

    // 검색 조건 필드들
    private String scSessionId;
    private String scName;
    private String scEmail;
    private String scJoinTime;
    private String scTenantId;
    private String scDept;
    private String sortOrder = "DESC";

    // 세션 정보 필드들
    private String sessionName;
    private String sessionDept;
    private String sessionStartTime;
    private String sessionInviteLink;
    private String sessionLinkExpiry;

    // 페이징 관련 필드들
    private int pageSize = 9999;
    private long pageIndex = 1;
    private int startRow = 0;

    // 통계 관련 필드들
    private long totalCount;
    private int activeCount;
    private int leftCount;
    private double avgMinutes;

    // 기타 필드들
    private String tenantId;
    private String cud;
    private String rowStatus;

    // 기본 생성자
    public AttendanceVO() {}

    // Getter & Setter 메소드들
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public String getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }

    public String getParticipationMinutes() {
        return participationMinutes;
    }

    public void setParticipationMinutes(String participationMinutes) {
        this.participationMinutes = participationMinutes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // 검색 조건 필드들
    public String getScSessionId() {
        return scSessionId;
    }

    public void setScSessionId(String scSessionId) {
        this.scSessionId = scSessionId;
    }

    public String getScName() {
        return scName;
    }

    public void setScName(String scName) {
        this.scName = scName;
    }

    public String getScEmail() {
        return scEmail;
    }

    public void setScEmail(String scEmail) {
        this.scEmail = scEmail;
    }

    public String getScJoinTime() {
        return scJoinTime;
    }

    public void setScJoinTime(String scJoinTime) {
        this.scJoinTime = scJoinTime;
    }

    public String getScTenantId() {
        return scTenantId;
    }

    public void setScTenantId(String scTenantId) {
        this.scTenantId = scTenantId;
    }

    public String getScDept() {
        return scDept;
    }

    public void setScDept(String scDept) {
        this.scDept = scDept;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    // 세션 정보 필드들
    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionDept() {
        return sessionDept;
    }

    public void setSessionDept(String sessionDept) {
        this.sessionDept = sessionDept;
    }

    public String getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(String sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public String getSessionInviteLink() {
        return sessionInviteLink;
    }

    public void setSessionInviteLink(String sessionInviteLink) {
        this.sessionInviteLink = sessionInviteLink;
    }

    public String getSessionLinkExpiry() {
        return sessionLinkExpiry;
    }

    public void setSessionLinkExpiry(String sessionLinkExpiry) {
        this.sessionLinkExpiry = sessionLinkExpiry;
    }

    // 페이징 관련 필드들
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    // 통계 관련 필드들
    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public int getLeftCount() {
        return leftCount;
    }

    public void setLeftCount(int leftCount) {
        this.leftCount = leftCount;
    }

    public double getAvgMinutes() {
        return avgMinutes;
    }

    public void setAvgMinutes(double avgMinutes) {
        this.avgMinutes = avgMinutes;
    }

    // 기타 필드들
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getCud() {
        return cud;
    }

    public void setCud(String cud) {
        this.cud = cud;
    }

    public String getRowStatus() {
        return rowStatus;
    }

    public void setRowStatus(String rowStatus) {
        this.rowStatus = rowStatus;
    }

    @Override
    public String toString() {
        return "AttendanceVO{" +
                "recordId='" + recordId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", joinTime='" + joinTime + '\'' +
                ", leaveTime='" + leaveTime + '\'' +
                ", participationMinutes='" + participationMinutes + '\'' +
                ", status='" + status + '\'' +
                ", scSessionId='" + scSessionId + '\'' +
                ", scName='" + scName + '\'' +
                ", scEmail='" + scEmail + '\'' +
                ", scJoinTime='" + scJoinTime + '\'' +
                ", scTenantId='" + scTenantId + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                ", sessionName='" + sessionName + '\'' +
                ", sessionDept='" + sessionDept + '\'' +
                ", sessionStartTime='" + sessionStartTime + '\'' +
                ", pageSize=" + pageSize +
                ", pageIndex=" + pageIndex +
                ", totalCount=" + totalCount +
                ", activeCount=" + activeCount +
                ", leftCount=" + leftCount +
                ", avgMinutes=" + avgMinutes +
                '}';
    }
}