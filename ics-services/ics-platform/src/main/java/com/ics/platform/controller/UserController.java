package com.ics.platform.controller;

import com.ics.common.constants.CommonConstants;
import com.ics.common.result.PageResult;
import com.ics.common.result.Result;
import com.ics.platform.dto.UserRequest;
import com.ics.platform.entity.User;
import com.ics.platform.service.UserService;
import com.ics.security.context.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户 CRUD 操作")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<User> create(@Valid @RequestBody UserRequest request) {
        String tenantId = TenantContext.getTenantId();
        User user = userService.create(Long.parseLong(tenantId), request);
        return Result.success(user);
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        User user = userService.update(id, request);
        return Result.success(user);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @Operation(summary = "查询用户详情")
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    @Operation(summary = "分页查询用户")
    @GetMapping
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword
    ) {
        String tenantId = TenantContext.getTenantId();
        PageResult<User> result = userService.list(pageNum, pageSize, Long.parseLong(tenantId), keyword);
        return Result.success(result);
    }
}
