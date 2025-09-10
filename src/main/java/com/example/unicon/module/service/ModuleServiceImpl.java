package com.example.unicon.module.service;

import com.example.unicon.module.dto.ModuleDetailDto;
import com.example.unicon.module.dto.ModuleUsageDto;
import com.example.unicon.module.mapper.ModuleMapper;
import com.example.unicon.module.vo.ModuleUsageVO;
import com.example.unicon.module.vo.ModuleVO;
import com.example.unicon.module.vo.TenantModuleDetailVO;
import com.example.unicon.module.vo.TenantModuleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ModuleServiceImpl implements ModuleService {

    private final ModuleMapper moduleMapper;

    public ModuleServiceImpl(ModuleMapper moduleMapper) {
        this.moduleMapper = moduleMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleDetailDto> getTenantModules(Integer tenantId) {
        List<TenantModuleDetailVO> moduleDetails = moduleMapper.findModulesByTenantId(tenantId);
        return moduleDetails.stream()
                .map(ModuleDetailDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    // 👇 [수정] tenantId 파라미터 추가
    public List<ModuleUsageDto> getModuleUsage(String moduleId, Integer tenantId) {
        // 👇 [수정] Mapper 호출 시 tenantId 전달
        List<ModuleUsageVO> usageVOs = moduleMapper.findUsageByModuleId(moduleId, tenantId);

        return usageVOs.stream()
                .map(vo -> new ModuleUsageDto(vo.getName(), vo.getStartTime()))
                .collect(Collectors.toList());
    }

    /**
     * 모듈정보 목록을 조회합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ModuleVO> selectListModule(ModuleVO moduleVo) throws Exception {
        log.debug("ModuleServiceImpl.selectListModule - pageSize: {}, pageIndex: {}",
                moduleVo.getPageSize(), moduleVo.getPageIndex());

        List<ModuleVO> list = moduleMapper.selectListModule(moduleVo);
        log.debug("ModuleServiceImpl.selectListModule - result count: {}", list.size());

        return list;
    }

    /**
     * 조회한 모듈정보 전체 카운트
     */
    @Override
    @Transactional(readOnly = true)
    public long selectListCountModule(ModuleVO moduleVo) {
        long count = moduleMapper.selectListCountModule(moduleVo);
        log.debug("ModuleServiceImpl.selectListCountModule - total count: {}", count);
        return count;
    }

    /**
     * 모듈정보를 상세 조회한다.
     */
    @Override
    @Transactional(readOnly = true)
    public ModuleVO selectModule(ModuleVO moduleVo) {
        log.debug("ModuleServiceImpl.selectModule - moduleId: {}", moduleVo.getModuleId());

        ModuleVO resultVO = moduleMapper.selectModule(moduleVo);
        if (resultVO == null) {
            throw new RuntimeException("해당 모듈을 찾을 수 없습니다. moduleId: " + moduleVo.getModuleId());
        }

        return resultVO;
    }

    /**
     * 모듈 구독 처리
     */
    @Override
    public int subscribeModule(TenantModuleVO tenantModuleVo) throws Exception {
        log.debug("모듈 구독 처리 시작: {}", tenantModuleVo.toString());

        try {
            // 유효성 검사
            if (tenantModuleVo.getModuleId() == null || tenantModuleVo.getModuleId().trim().isEmpty()) {
                throw new RuntimeException("모듈 ID가 필요합니다.");
            }
            if (tenantModuleVo.getTenantId() == null || tenantModuleVo.getTenantId().trim().isEmpty()) {
                throw new RuntimeException("테넌트 ID가 필요합니다.");
            }

            // 모듈 존재 여부 확인
            ModuleVO moduleVo = new ModuleVO();
            moduleVo.setModuleId(tenantModuleVo.getModuleId());
            ModuleVO existingModule = moduleMapper.selectModule(moduleVo);
            if (existingModule == null) {
                throw new RuntimeException("존재하지 않는 모듈입니다: " + tenantModuleVo.getModuleId());
            }

            // 중복 구독 체크
            if (isDuplicateSubscription(tenantModuleVo)) {
                throw new RuntimeException("이미 구독중인 모듈입니다.");
            }

            // 구매일시 설정 (자동으로 현재 시간 설정)
            if (tenantModuleVo.getPurchasedAt() == null || tenantModuleVo.getPurchasedAt().trim().isEmpty()) {
                tenantModuleVo.setPurchasedAt(null); // SQL에서 CURRENT_TIMESTAMP 사용
            }

            int result = moduleMapper.insertTenantModule(tenantModuleVo);

            if (result > 0) {
                log.debug("모듈 구독 성공: moduleId={}, tenantId={}",
                        tenantModuleVo.getModuleId(), tenantModuleVo.getTenantId());
            } else {
                throw new RuntimeException("모듈 구독 처리에 실패했습니다.");
            }

            return result;

        } catch (Exception e) {
            log.error("모듈 구독 처리 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 모듈 구독 해지 처리
     */
    @Override
    @Transactional
    public int unsubscribeModule(TenantModuleVO tenantModuleVo) throws Exception {
        log.debug("모듈 구독 해지 처리 시작: {}", tenantModuleVo.toString());

        try {
            // 유효성 검사
            if (tenantModuleVo.getModuleId() == null || tenantModuleVo.getModuleId().trim().isEmpty()) {
                throw new RuntimeException("모듈 ID가 필요합니다.");
            }
            if (tenantModuleVo.getTenantId() == null || tenantModuleVo.getTenantId().trim().isEmpty()) {
                throw new RuntimeException("테넌트 ID가 필요합니다.");
            }

            // 구독 상태 확인
            if (!isModuleSubscribed(tenantModuleVo)) {
                throw new RuntimeException("구독하지 않은 모듈입니다.");
            }

            int result = moduleMapper.deleteTenantModule(tenantModuleVo);

            if (result > 0) {
                log.debug("모듈 구독 해지 성공: moduleId={}, tenantId={}",
                        tenantModuleVo.getModuleId(), tenantModuleVo.getTenantId());
            } else {
                throw new RuntimeException("모듈 구독 해지 처리에 실패했습니다.");
            }

            return result;

        } catch (Exception e) {
            log.error("모듈 구독 해지 처리 중 오류 발생", e);
            throw e;
        }
    }


    /**
     * 테넌트의 구독 모듈 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<TenantModuleVO> selectSubscribedModules(TenantModuleVO tenantModuleVo) throws Exception {
        log.debug("구독 모듈 목록 조회: tenantId={}", tenantModuleVo.getTenantId());

        try {
            if (tenantModuleVo.getTenantId() == null || tenantModuleVo.getTenantId().trim().isEmpty()) {
                throw new RuntimeException("테넌트 ID가 필요합니다.");
            }

            List<TenantModuleVO> result = moduleMapper.selectTenantModules(tenantModuleVo);
            log.debug("구독 모듈 조회 결과: {}개", result.size());

            return result;

        } catch (Exception e) {
            log.error("구독 모듈 목록 조회 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 모듈 구독 상태 확인
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isModuleSubscribed(TenantModuleVO tenantModuleVo) throws Exception {
        try {
            TenantModuleVO result = moduleMapper.selectTenantModule(tenantModuleVo);
            boolean subscribed = result != null;
            log.debug("모듈 구독 상태: moduleId={}, tenantId={}, subscribed={}",
                    tenantModuleVo.getModuleId(), tenantModuleVo.getTenantId(), subscribed);
            return subscribed;
        } catch (Exception e) {
            log.error("모듈 구독 상태 확인 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 중복 구독 체크
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicateSubscription(TenantModuleVO tenantModuleVo) throws Exception {
        return isModuleSubscribed(tenantModuleVo);
    }

    /**
     * 모듈정보를 등록 처리 한다.
     */
    @Override
    @Transactional
    public int insertModule(ModuleVO moduleVo) throws Exception {
        log.debug("ModuleServiceImpl.insertModule - moduleVo: {}", moduleVo);

        // 중복 체크
        ModuleVO existingModule = null;
        try {
            existingModule = moduleMapper.selectModule(moduleVo);
        } catch (Exception e) {
            // 조회 실패는 중복이 아니므로 계속 진행
        }

        if (existingModule != null) {
            throw new RuntimeException("이미 존재하는 모듈 ID입니다: " + moduleVo.getModuleId());
        }

        return moduleMapper.insertModule(moduleVo);
    }

    /**
     * 모듈정보를 갱신 처리 한다.
     */
    @Override
    public int updateModule(ModuleVO moduleVo) throws Exception {
        log.debug("ModuleServiceImpl.updateModule - moduleVo: {}", moduleVo);

        // 존재 여부 확인
        ModuleVO existingModule = moduleMapper.selectModule(moduleVo);
        if (existingModule == null) {
            throw new RuntimeException("해당 모듈을 찾을 수 없습니다. moduleId: " + moduleVo.getModuleId());
        }

        return moduleMapper.updateModule(moduleVo);
    }

    /**
     * 모듈정보를 삭제 처리 한다.
     */
    @Override
    @Transactional
    public int deleteModule(ModuleVO moduleVo) throws Exception {
        log.debug("ModuleServiceImpl.deleteModule - moduleId: {}", moduleVo.getModuleId());

        // 존재 여부 확인
        ModuleVO existingModule = moduleMapper.selectModule(moduleVo);
        if (existingModule == null) {
            throw new RuntimeException("해당 모듈을 찾을 수 없습니다. moduleId: " + moduleVo.getModuleId());
        }

        return moduleMapper.deleteModule(moduleVo);
    }


}