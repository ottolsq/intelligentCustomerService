package com.ics.ai.service;

import com.ics.ai.dto.PromptTemplateRequest;
import com.ics.ai.entity.PromptTemplate;

/**
 * 提示词模板服务
 */
public interface PromptTemplateService {

    /**
     * 创建模板
     */
    PromptTemplate create(PromptTemplateRequest request);

    /**
     * 更新模板
     */
    PromptTemplate update(Long id, PromptTemplateRequest request);

    /**
     * 删除模板
     */
    void delete(Long id);

    /**
     * 根据 ID 查询
     */
    PromptTemplate getById(Long id);

    /**
     * 根据编码查询（获取最新启用版本）
     */
    PromptTemplate getByCode(String templateCode);

    /**
     * 渲染模板（替换占位符）
     */
    String render(String templateCode, java.util.Map<String, String> variables);
}
