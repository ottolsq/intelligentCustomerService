package com.ics.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ics.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 使用日志实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_usage_log")
public class AiUsageLog extends BaseEntity {

    /**
     * 会话 ID
     */
    private Long conversationId;

    /**
     * 用户消息内容
     */
    private String userMessage;

    /**
     * AI 回复内容
     */
    private String aiResponse;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * 意图分类结果
     */
    private String intent;

    /**
     * 置信度 (0-1)
     */
    private Double confidence;

    /**
     * 请求 Token 数
     */
    private Integer promptTokens;

    /**
     * 响应 Token 数
     */
    private Integer completionTokens;

    /**
     * 总 Token 数
     */
    private Integer totalTokens;

    /**
     * 耗时（毫秒）
     */
    private Long latencyMs;

    /**
     * 使用的提示词模板编码
     */
    private String templateCode;

    /**
     * 状态: success / error / fallback
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMessage;
}
