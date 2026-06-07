package com.ics.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户创建/更新请求
 */
@Data
public class UserRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @Size(min = 6, max = 64, message = "密码长度必须在 6-64 之间")
    private String password;

    private String nickname;
    private String email;
    private String phone;
    private String avatar;

    /**
     * 状态: 0=禁用, 1=启用
     */
    private Integer status = 1;

    /**
     * 角色 ID 列表
     */
    private java.util.List<Long> roleIds;
}
