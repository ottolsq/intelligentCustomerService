package com.ics.platform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ics.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant")
public class Tenant extends BaseEntity {

    /**
     * 租户编码（唯一标识）
     */
    private String tenantCode;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 套餐类型: free / professional / enterprise
     */
    private String planType;

    /**
     * 状态: 0=禁用, 1=启用
     */
    private Integer status;

    /**
     * 过期时间
     */
    private String expireTime;

    /**
     * 备注
     */
    private String remark;
}
