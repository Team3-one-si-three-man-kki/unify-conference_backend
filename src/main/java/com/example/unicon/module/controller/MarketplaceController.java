package com.example.unicon.module.controller;

import com.example.unicon.module.service.ModuleService;
import com.example.unicon.module.vo.ModuleVO;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/marketplace")
@CrossOrigin(origins = "*")
public class MarketplaceController {

	@Resource(name = "moduleServiceImpl")
	private ModuleService moduleService;


	/**
	 * 마켓플레이스용 모듈 목록 조회
	 */
	@GetMapping("/modules")
	public ResponseEntity<Map<String, Object>> getModuleList(
			@RequestParam(defaultValue = "50") int pageSize,
			@RequestParam(defaultValue = "1") int pageIndex,
			@RequestParam(required = false) String tenantId) {

		try {
			ModuleVO moduleVo = new ModuleVO();
			moduleVo.setPageSize(pageSize);
			moduleVo.setPageIndex(pageIndex);

			List<ModuleVO> moduleList = moduleService.selectListModule(moduleVo);
			long totalCount = moduleService.selectListCountModule(moduleVo);

			Map<String, Object> result = new HashMap<>();
			result.put("moduleVoList", moduleList);
			result.put("totalCount", totalCount);
			result.put("pageSize", pageSize);
			result.put("pageIndex", pageIndex);
			result.put("success", true);

			return ResponseEntity.ok(result);

		} catch (Exception e) {
			Map<String, Object> errorResult = new HashMap<>();
			errorResult.put("moduleVoList", List.of());
			errorResult.put("totalCount", 0);
			errorResult.put("pageSize", pageSize);
			errorResult.put("pageIndex", pageIndex);
			errorResult.put("success", false);
			errorResult.put("error", "모듈 목록 조회 중 오류가 발생했습니다: " + e.getMessage());

			return ResponseEntity.internalServerError().body(errorResult);
		}
	}

	/**
	 * 특정 모듈 상세 조회
	 */
	@GetMapping("/modules/{moduleId}")
	public ResponseEntity<Map<String, Object>> getModule(@PathVariable String moduleId) {
		try {
			ModuleVO moduleVo = new ModuleVO();
			moduleVo.setModuleId(moduleId);

			ModuleVO result = moduleService.selectModule(moduleVo);

			Map<String, Object> response = new HashMap<>();
			response.put("module", result);
			response.put("success", true);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			Map<String, Object> errorResult = new HashMap<>();
			errorResult.put("success", false);
			errorResult.put("error", "모듈 조회 중 오류가 발생했습니다: " + e.getMessage());

			return ResponseEntity.internalServerError().body(errorResult);
		}
	}
}