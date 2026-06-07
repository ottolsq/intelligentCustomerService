package com.ics.security.context;

/**
 * 租户上下文
 * 通过 ThreadLocal 在当前请求线程中传递租户信息
 */
public final class TenantContext {

    private TenantContext() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final ThreadLocal<String> CURRENT_TENANT_ID = new ThreadLocal<>();

    /**
     * 设置当前租户 ID
     */
    public static void setTenantId(String tenantId) {
        CURRENT_TENANT_ID.set(tenantId);
    }

    /**
     * 获取当前租户 ID
     */
    public static String getTenantId() {
        return CURRENT_TENANT_ID.get();
    }

    /**
     * 清除租户上下文（请求结束后调用）
     */
    public static void clear() {
        CURRENT_TENANT_ID.remove();
    }
}
