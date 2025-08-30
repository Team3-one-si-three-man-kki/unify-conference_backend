package com.example.unicon.user.vo;

import java.util.List;

/**
 * 사용자 목록 응답을 담는 VO 클래스
 */
public class UserListVo {
    private static final long serialVersionUID = 1L;

    private List<UserVO> userVoList;
    private List<UserVO> saveDataList;
    private long totalCount;
    private int pageSize;
    private int pageIndex;

    public UserListVo() {
    }

    public List<UserVO> getUserVoList() {
        return userVoList;
    }

    public void setUserVoList(List<UserVO> userVoList) {
        this.userVoList = userVoList;
    }

    public List<UserVO> getSaveDataList() {
        return saveDataList;
    }

    public void setSaveDataList(List<UserVO> saveDataList) {
        this.saveDataList = saveDataList;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    @Override
    public String toString() {
        return "UserListVo{" +
                "userVoList=" + userVoList +
                ", saveDataList=" + saveDataList +
                ", totalCount=" + totalCount +
                ", pageSize=" + pageSize +
                ", pageIndex=" + pageIndex +
                '}';
    }
}