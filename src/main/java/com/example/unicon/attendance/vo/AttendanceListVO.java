package com.example.unicon.attendance.vo;

import java.io.Serializable;
import java.util.List;

public class AttendanceListVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<AttendanceVO> attendanceVoList;
    private long totalCount;
    private long pageIndex = 1;
    private int totalPages;
    private int currentPage = 1;
    private int pageSize = 9999;

    // 기본 생성자
    public AttendanceListVO() {
    }

    // 생성자
    public AttendanceListVO(List<AttendanceVO> attendanceVoList, long totalCount) {
        this.attendanceVoList = attendanceVoList;
        this.totalCount = totalCount;
    }

    // Getter & Setter 메소드들
    public List<AttendanceVO> getAttendanceVoList() {
        return attendanceVoList;
    }

    public void setAttendanceVoList(List<AttendanceVO> attendanceVoList) {
        this.attendanceVoList = attendanceVoList;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        // totalPages 자동 계산
        if (this.totalCount > 0 && pageSize > 0) {
            this.totalPages = (int) Math.ceil((double) this.totalCount / pageSize);
        }
    }

    // 편의 메소드들
    public boolean hasData() {
        return attendanceVoList != null && !attendanceVoList.isEmpty();
    }

    public int getDataSize() {
        return attendanceVoList != null ? attendanceVoList.size() : 0;
    }

    public boolean hasNextPage() {
        return currentPage < totalPages;
    }

    public boolean hasPreviousPage() {
        return currentPage > 1;
    }

    @Override
    public String toString() {
        return "AttendanceListVO{" +
                "attendanceVoList=" + (attendanceVoList != null ? attendanceVoList.size() + " items" : "null") +
                ", totalCount=" + totalCount +
                ", pageIndex=" + pageIndex +
                ", totalPages=" + totalPages +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                '}';
    }
}