package com.ics.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ics.platform.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名和租户 ID 查询用户
     */
    User selectByUsernameAndTenantId(@Param("username") String username, @Param("tenantId") Long tenantId);
}
