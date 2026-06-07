package com.ics.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.*;
import com.ics.security.context.TenantContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 配置
 * 包含：分页拦截器、租户拦截器、自动填充
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 租户拦截器（从 TenantContext 获取当前租户 ID）
        interceptor.addInnerInterceptor(tenantLineInnerInterceptor());

        // 分页拦截器
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 单页最大限制，防止查询全部数据
        paginationInnerInterceptor.setMaxLimit(500L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        return interceptor;
    }

    /**
     * 租户行拦截器
     */
    @Bean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor() {
        return new TenantLineInnerInterceptor(new com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler() {
            @Override
            public net.sf.jsqlparser.expression.Expression getTenantId() {
                String tenantId = TenantContext.getTenantId();
                if (tenantId != null) {
                    return new net.sf.jsqlparser.expression.StringValue(tenantId);
                }
                // 如果无租户上下文，返回一个不可能的值避免泄漏数据
                return new net.sf.jsqlparser.expression.StringValue("__no_tenant__");
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            @Override
            public boolean ignoreTable(String tableName) {
                // 所有表都启用租户隔离（可根据需要排除某些表）
                return false;
            }
        });
    }

    /**
     * 自动填充处理器
     * 自动填充 createTime, updateTime, createBy, updateBy
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                // createBy/updateBy 从用户上下文中获取，这里暂设为 0
                this.strictInsertFill(metaObject, "createBy", Long.class, 0L);
                this.strictInsertFill(metaObject, "updateBy", Long.class, 0L);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                this.strictUpdateFill(metaObject, "updateBy", Long.class, 0L);
            }
        };
    }
}
