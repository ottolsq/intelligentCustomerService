package com.ics.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 客户端错误
     */
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),

    /**
     * 服务端错误
     */
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    /**
     * 业务错误码（1000 起）
     */
    TENANT_NOT_FOUND(1001, "租户不存在"),
    TENANT_DISABLED(1002, "租户已禁用"),
    USER_NOT_FOUND(1003, "用户不存在"),
    USER_DISABLED(1004, "用户已禁用"),
    USERNAME_DUPLICATE(1005, "用户名已存在"),
    PASSWORD_INCORRECT(1006, "密码错误"),
    TOKEN_EXPIRED(1007, "Token 已过期"),
    TOKEN_INVALID(1008, "Token 无效"),

    /**
     * 限流
     */
    RATE_LIMIT_EXCEEDED(429, "请求过于频繁，请稍后重试");

    private final Integer code;
    private final String message;
}
