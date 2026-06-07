package com.ics.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 回复响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResponse {

    /**
     * AI 回复内容
     */
    private String reply;

    /**
     * 意图分类结果
     */
    private String intent;

    /**
     * 置信度 (0-1)
     */
    private Double confidence;

    /**
     * 是否需要转人工
     */
    private Boolean needsEscalation;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * Token 使用量
     */
    private Integer totalTokens;

    /**
     * 响应耗时（毫秒）
     */
    private Long latencyMs;
}
