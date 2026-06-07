package com.ics.platform.service;

import com.ics.common.result.PageResult;
import com.ics.platform.dto.UserRequest;
import com.ics.platform.entity.User;

/**
 * 用户服务
 */
public interface UserService {

    /**
     * 创建用户
     */
    User create(Long tenantId, UserRequest request);

    /**
     * 更新用户
     */
    User update(Long id, UserRequest request);

    /**
     * 删除用户
     */
    void delete(Long id);

    /**
     * 根据 ID 查询用户
     */
    User getById(Long id);

    /**
     * 根据用户名和租户 ID 查询用户
     */
    User getByUsername(String username, Long tenantId);

    /**
     * 分页查询用户
     */
    PageResult<User> list(int pageNum, int pageSize, Long tenantId, String keyword);
}
