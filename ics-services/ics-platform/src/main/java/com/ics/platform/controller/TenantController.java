package com.ics.platform.controller;

import com.ics.common.constants.CommonConstants;
import com.ics.common.result.PageResult;
import com.ics.common.result.Result;
import com.ics.platform.dto.TenantRequest;
import com.ics.platform.entity.Tenant;
import com.ics.platform.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 租户控制器
 */
@Tag(name = "租户管理", description = "租户 CRUD 操作")
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @Operation(summary = "创建租户")
    @PostMapping
    public Result<Tenant> create(@Valid @RequestBody TenantRequest request) {
        Tenant tenant = tenantService.create(request);
        return Result.success(tenant);
    }

    @Operation(summary = "更新租户")
    @PutMapping("/{id}")
    public Result<Tenant> update(@PathVariable Long id, @Valid @RequestBody TenantRequest request) {
        Tenant tenant = tenantService.update(id, request);
        return Result.success(tenant);
    }

    @Operation(summary = "删除租户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tenantService.delete(id);
        return Result.success();
    }

    @Operation(summary = "查询租户详情")
    @GetMapping("/{id}")
    public Result<Tenant> getById(@PathVariable Long id) {
        Tenant tenant = tenantService.getById(id);
        return Result.success(tenant);
    }

    @Operation(summary = "分页查询租户")
    @GetMapping
    public Result<PageResult<Tenant>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword
    ) {
        PageResult<Tenant> result = tenantService.list(pageNum, pageSize, keyword);
        return Result.success(result);
    }
}
