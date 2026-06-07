package com.ics.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 意图分类请求
 */
@Data
public class IntentRequest {

    /**
     * 用户消息
     */
    @NotBlank(message = "用户消息不能为空")
    private String message;

    /**
     * 上下文（可选，用于多轮对话）
     */
    private String context;
}
