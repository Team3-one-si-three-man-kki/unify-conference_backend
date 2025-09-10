package com.example.unicon.domain.sessionModule.mapper;

import com.example.unicon.domain.sessionModule.model.SessionModule;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SessionModuleMapper {
    void insertSessionModule(SessionModule sessionModule);
    void updateSessionModule(SessionModule sessionModule);
    void deleteSessionModule(Integer sessionId, Integer moduleId);
    void deleteAllBySessionId(Integer sessionId);
    
    SessionModule findBySessionIdAndModuleId(Integer sessionId, Integer moduleId);
    List<SessionModule> findBySessionId(Integer sessionId);
    List<SessionModule> findByModuleId(Integer moduleId);
}