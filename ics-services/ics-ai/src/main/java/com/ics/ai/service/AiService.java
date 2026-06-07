package com.ics.ai.service;

import com.ics.ai.dto.AiChatRequest;
import com.ics.ai.dto.AiChatResponse;
import com.ics.ai.dto.IntentRequest;
import com.ics.ai.dto.IntentResponse;

/**
 * AI 服务接口
 */
public interface AiService {

    /**
     * 智能回复（完整流程：意图分类 → RAG/LLM 生成）
     *
     * @param request 用户请求
     * @return AI 回复
     */
    AiChatResponse chat(AiChatRequest request);

    /**
     * 意图分类
     *
     * @param request 意图分类请求
     * @return 意图分类结果
     */
    IntentResponse classifyIntent(IntentRequest request);

    /**
     * 生成回复（使用 LLM）
     *
     * @param message  用户消息
     * @param intent   意图
     * @param context  上下文（可选 FAQ 内容）
     * @return AI 回复内容
     */
    String generateReply(String message, String intent, String context);
}
