package com.ics.common.constants;

/**
 * 公共常量定义
 */
public final class CommonConstants {

    private CommonConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== HTTP Header ====================

    /**
     * 租户 ID 请求头
     */
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";

    /**
     * API Key 请求头
     */
    public static final String HEADER_API_KEY = "X-API-Key";

    /**
     * 链路追踪 ID
     */
    public static final String HEADER_TRACE_ID = "X-Trace-Id";

    /**
     * 认证 Token 请求头
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    // ==================== JWT Claims ====================

    /**
     * JWT 中租户 ID 的 claim key
     */
    public static final String JWT_CLAIM_TENANT_ID = "tenantId";

    /**
     * JWT 中用户 ID 的 claim key
     */
    public static final String JWT_CLAIM_USER_ID = "userId";

    /**
     * JWT 中角色列表的 claim key
     */
    public static final String JWT_CLAIM_ROLES = "roles";

    /**
     * JWT 中权限列表的 claim key
     */
    public static final String JWT_CLAIM_PERMISSIONS = "permissions";

    // ==================== 分页常量 ====================

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 最大每页大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    // ==================== 通用常量 ====================

    public static final String UTF_8 = "UTF-8";

    /**
     * 未删除状态
     */
    public static final int DELETED_NO = 0;

    /**
     * 已删除状态
     */
    public static final int DELETED_YES = 1;

    /**
     * Redis Key 前缀分隔符
     */
    public static final String REDIS_KEY_SEPARATOR = ":";

    /**
     * 租户缓存前缀
     */
    public static final String REDIS_TENANT_PREFIX = "ics:tenant:";

    /**
     * JWT Token 黑名单前缀
     */
    public static final String REDIS_TOKEN_BLACKLIST_PREFIX = "ics:token:blacklist:";
}
