package com.ics.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ics.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 提示词模板实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_prompt_template")
public class PromptTemplate extends BaseEntity {

    /**
     * 模板编码（唯一标识）
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板内容（支持占位符 {variable}）
     */
    private String content;

    /**
     * 用途分类: intent_classification / response_generation / faq_answer / escalation
     */
    private String category;

    /**
     * 模型名称（如 gpt-4o-mini, gpt-4o）
     */
    private String modelName;

    /**
     * 温度参数
     */
    private Double temperature;

    /**
     * 最大 Token 数
     */
    private Integer maxTokens;

    /**
     * 状态: 0=禁用, 1=启用
     */
    private Integer status;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 备注
     */
    private String remark;
}
