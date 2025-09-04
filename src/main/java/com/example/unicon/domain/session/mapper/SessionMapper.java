package com.example.unicon.domain.session.mapper;

import com.example.unicon.domain.session.model.Session;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SessionMapper {
    void insertSession(Session session);
    void updateSession(Session session);
    Session findSessionById(Integer sessionId);
    List<Session> findSessionsByTenantId(Integer tenantId);
}