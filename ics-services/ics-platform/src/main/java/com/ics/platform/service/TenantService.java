package com.ics.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ics.common.result.PageResult;
import com.ics.platform.dto.TenantRequest;
import com.ics.platform.entity.Tenant;

/**
 * 租户服务
 */
public interface TenantService {

    /**
     * 创建租户
     */
    Tenant create(TenantRequest request);

    /**
     * 更新租户
     */
    Tenant update(Long id, TenantRequest request);

    /**
     * 删除租户
     */
    void delete(Long id);

    /**
     * 根据 ID 查询租户
     */
    Tenant getById(Long id);

    /**
     * 根据租户编码查询
     */
    Tenant getByTenantCode(String tenantCode);

    /**
     * 分页查询租户
     */
    PageResult<Tenant> list(int pageNum, int pageSize, String keyword);
}
