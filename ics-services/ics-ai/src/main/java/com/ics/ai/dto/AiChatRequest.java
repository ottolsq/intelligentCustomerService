package com.ics.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI 回复请求
 */
@Data
public class AiChatRequest {

    /**
     * 会话 ID
     */
    private Long conversationId;

    /**
     * 用户消息
     */
    @NotBlank(message = "用户消息不能为空")
    private String message;

    /**
     * 租户 ID（由 Gateway 注入）
     */
    private Long tenantId;
}
