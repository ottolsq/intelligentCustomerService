package com.ics.platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 租户创建/更新请求
 */
@Data
public class TenantRequest {

    @NotBlank(message = "租户编码不能为空")
    private String tenantCode;

    @NotBlank(message = "租户名称不能为空")
    private String tenantName;

    private String contactName;
    private String contactPhone;
    private String contactEmail;

    /**
     * 套餐类型: free / professional / enterprise
     */
    private String planType = "free";

    /**
     * 状态: 0=禁用, 1=启用
     */
    private Integer status = 1;

    private String expireTime;
    private String remark;
}
