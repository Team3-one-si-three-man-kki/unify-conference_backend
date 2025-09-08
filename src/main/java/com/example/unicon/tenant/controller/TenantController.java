package com.example.unicon.tenant.controller;

import com.example.unicon.tenant.service.TenantService;
import com.example.unicon.tenant.vo.TenantVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword
    ) {
        List<TenantVO> items = tenantService.findTenants(page, size, keyword);
        long total = tenantService.countTenants(keyword);
        return ResponseEntity.ok(Map.of(
                "page", page,
                "size", size,
                "total", total,
                "items", items
        ));
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<TenantVO> detail(@PathVariable Integer tenantId) {
        return ResponseEntity.ok(tenantService.findById(tenantId));
    }

    @PostMapping
    public ResponseEntity<TenantVO> create(@RequestBody TenantVO req) {
        TenantVO saved = tenantService.create(req);
        return ResponseEntity.created(URI.create("/api/tenants/" + saved.getTenantId())).body(saved);
    }

    @PutMapping("/{tenantId}")
    public ResponseEntity<TenantVO> update(@PathVariable Integer tenantId, @RequestBody TenantVO req) {
        req.setTenantId(tenantId);
        return ResponseEntity.ok(tenantService.update(req));
    }

    @DeleteMapping("/{tenantId}")
    public ResponseEntity<Void> delete(@PathVariable Integer tenantId) {
        tenantService.delete(tenantId);
        return ResponseEntity.noContent().build();
    }
}
