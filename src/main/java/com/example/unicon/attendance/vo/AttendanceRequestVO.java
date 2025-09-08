package com.example.unicon.attendance.vo;

import java.io.Serializable;

public class AttendanceRequestVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private AttendanceVO attendanceVo;

    // 기본 생성자
    public AttendanceRequestVO() {}

    // 생성자
    public AttendanceRequestVO(AttendanceVO attendanceVo) {
        this.attendanceVo = attendanceVo;
    }

    // Getter & Setter
    public AttendanceVO getAttendanceVo() {
        return attendanceVo;
    }

    public void setAttendanceVo(AttendanceVO attendanceVo) {
        this.attendanceVo = attendanceVo;
    }

    @Override
    public String toString() {
        return "AttendanceRequestVO{" +
                "attendanceVo=" + attendanceVo +
                '}';
    }
}

/**
 * API 응답 래퍼 클래스
 */
class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T data;
    private String message;
    private boolean success;

    public ApiResponse() {}

    public ApiResponse(T data, String message) {
        this.data = data;
        this.message = message;
        this.success = true;
    }

    public ApiResponse(T data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    // 정적 팩토리 메소드들
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message, true);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, "성공", true);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(null, message, false);
    }

    public static <T> ApiResponse<T> error(T data, String message) {
        return new ApiResponse<>(data, message, false);
    }

    // Getter & Setter
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}