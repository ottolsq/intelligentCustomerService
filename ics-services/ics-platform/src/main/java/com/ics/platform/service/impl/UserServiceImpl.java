package com.ics.platform.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ics.common.enums.ResultCode;
import com.ics.common.exception.BusinessException;
import com.ics.common.result.PageResult;
import com.ics.platform.dto.UserRequest;
import com.ics.platform.entity.User;
import com.ics.platform.entity.UserRole;
import com.ics.platform.mapper.UserMapper;
import com.ics.platform.mapper.UserRoleMapper;
import com.ics.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User create(Long tenantId, UserRequest request) {
        // 检查用户名是否已存在
        User existing = userMapper.selectByUsernameAndTenantId(request.getUsername(), tenantId);
        if (existing != null) {
            throw new BusinessException(ResultCode.USERNAME_DUPLICATE);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setStatus(request.getStatus());
        user.setTenantId(tenantId);

        userMapper.insert(user);

        // 关联角色
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            for (Long roleId : request.getRoleIds()) {
                UserRole ur = new UserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(roleId);
                ur.setTenantId(tenantId);
                userRoleMapper.insert(ur);
            }
        }

        return user;
    }

    @Override
    @Transactional
    public User update(Long id, UserRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setStatus(request.getStatus());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userMapper.updateById(user);

        // 更新角色关联
        if (request.getRoleIds() != null) {
            userRoleMapper.deleteByUserId(user.getId());
            for (Long roleId : request.getRoleIds()) {
                UserRole ur = new UserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(roleId);
                ur.setTenantId(user.getTenantId());
                userRoleMapper.insert(ur);
            }
        }

        return user;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        userRoleMapper.deleteByUserId(id);
        userMapper.deleteById(id);
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public User getByUsername(String username, Long tenantId) {
        return userMapper.selectByUsernameAndTenantId(username, tenantId);
    }

    @Override
    public PageResult<User> list(int pageNum, int pageSize, Long tenantId, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> result = userMapper.selectPage(page,
                Wrappers.<User>lambdaQuery()
                        .eq(User::getTenantId, tenantId)
                        .like(keyword != null && !keyword.isBlank(), User::getUsername, keyword)
                        .or(keyword != null && !keyword.isBlank(), q -> q.like(User::getNickname, keyword))
                        .orderByDesc(User::getCreateTime)
        );

        PageResult<User> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPages(result.getPages());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }
}
