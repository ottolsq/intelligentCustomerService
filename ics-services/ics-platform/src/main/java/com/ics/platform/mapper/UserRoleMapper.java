package com.ics.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ics.platform.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户-角色关联 Mapper
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户 ID 查询角色编码列表
     */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户 ID 删除所有关联
     */
    int deleteByUserId(@Param("userId") Long userId);
}
