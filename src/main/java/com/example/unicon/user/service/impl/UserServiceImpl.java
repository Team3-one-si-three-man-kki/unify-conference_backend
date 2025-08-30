package com.example.unicon.user.service.impl;

import com.example.unicon.user.dao.UserDAO;
import com.example.unicon.user.service.UserService;
import com.example.unicon.user.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 관리 서비스 구현체
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public List<UserVo> selectUsersByTenant(UserVo vo) throws Exception {
        try {
            return userDAO.selectUsersByTenant(vo);
        } catch (Exception e) {
            System.err.println("사용자 목록 조회 중 오류: " + e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public void saveUserList(List<UserVo> userList) throws Exception {
        if (userList == null || userList.isEmpty()) {
            return;
        }

        List<UserVo> insertList = new ArrayList<>();
        List<UserVo> updateList = new ArrayList<>();
        List<UserVo> deleteList = new ArrayList<>();

        // rowStatus별로 데이터 분리
        for (UserVo user : userList) {
            String rowStatus = user.getRowStatus();
            if ("C".equals(rowStatus)) {
                insertList.add(user);
            } else if ("U".equals(rowStatus)) {
                updateList.add(user);
            } else if ("D".equals(rowStatus)) {
                deleteList.add(user);
            }
        }

        // 배치 처리 실행
        if (!insertList.isEmpty()) {
            for (UserVo user : insertList) {
                userDAO.insertUser(user);
            }
        }

        if (!updateList.isEmpty()) {
            for (UserVo user : updateList) {
                userDAO.updateUser(user);
            }
        }

        if (!deleteList.isEmpty()) {
            for (UserVo user : deleteList) {
                userDAO.deleteUser(user);
            }
        }
    }

    @Override
    public boolean isEmailAvailableInTenant(String email, String tenantId) throws Exception {
        if (email == null || email.trim().isEmpty() || tenantId == null || tenantId.trim().isEmpty()) {
            return false;
        }

        try {
            UserVo searchVo = new UserVo();
            searchVo.setEmail(email);
            searchVo.setTenantId(tenantId);

            List<UserVo> existingUsers = userDAO.selectUsersByEmailAndTenant(searchVo);
            return existingUsers == null || existingUsers.isEmpty();

        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public int insertUser(UserVo userVo) throws Exception {
        return userDAO.insertUser(userVo);
    }

    @Override
    public int updateUser(UserVo userVo) throws Exception {
        return userDAO.updateUser(userVo);
    }

    @Override
    public int deleteUser(UserVo userVo) throws Exception {
        return userDAO.deleteUser(userVo);
    }

    @Override
    public UserVo selectUser(UserVo userVo) throws Exception {
        return userDAO.selectUser(userVo);
    }
}