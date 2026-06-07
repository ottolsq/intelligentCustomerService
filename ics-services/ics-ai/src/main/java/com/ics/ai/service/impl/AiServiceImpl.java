package com.ics.ai.service.impl;

import com.ics.ai.dto.AiChatRequest;
import com.ics.ai.dto.AiChatResponse;
import com.ics.ai.dto.IntentRequest;
import com.ics.ai.dto.IntentResponse;
import com.ics.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;

    @Override
    public AiChatResponse chat(AiChatRequest request) {
        long startTime = System.currentTimeMillis();
        log.debug("开始处理 AI 对话请求, conversationId={}", request.getConversationId());

        IntentRequest intentRequest = new IntentRequest();
        intentRequest.setMessage(request.getMessage());
        IntentResponse intentResponse = classifyIntent(intentRequest);
        String intent = intentResponse.getIntent();

        String reply = generateReply(request.getMessage(), intent, null);

        long latencyMs = System.currentTimeMillis() - startTime;
        log.debug("AI 对话请求处理完成, intent={}, latencyMs={}", intent, latencyMs);

        return AiChatResponse.builder()
                .reply(reply)
                .intent(intent)
                .confidence(intentResponse.getConfidence())
                .needsEscalation(false)
                .model("qwen-plus")
                .latencyMs(latencyMs)
                .build();
    }

    @Override
    public IntentResponse classifyIntent(IntentRequest request) {
        log.debug("开始意图分类, message={}", request.getMessage());

        String prompt = """
                你是一个意图分类器。请根据用户消息判断其意图类别。
                可选意图：FAQ咨询、投诉建议、业务办理、闲聊、转人工
                请以 JSON 格式返回：{"intent":"意图","confidence":0.95}
                
                用户消息：%s
                """.formatted(request.getMessage());

        ChatResponse response = chatClient.prompt()
                .user(prompt)
                .call()
                .chatResponse();

        String content = response.getResult().getOutput().getText();
        log.debug("意图分类结果: {}", content);

        return IntentResponse.builder()
                .intent(parseIntent(content))
                .confidence(parseConfidence(content))
                .candidates(List.of(
                        IntentResponse.IntentCandidate.builder()
                                .intent(parseIntent(content))
                                .confidence(parseConfidence(content))
                                .build()
                ))
                .build();
    }

    @Override
    public String generateReply(String message, String intent, String context) {
        log.debug("开始生成回复, intent={}", intent);

        String prompt;
        if (context != null && !context.isBlank()) {
            prompt = """
                    你是一个智能客服助手。请根据以下上下文信息回复用户。
                    
                    上下文：
                    %s
                    
                    用户消息：%s
                    识别意图：%s
                    """.formatted(context, message, intent);
        } else {
            prompt = """
                    你是一个智能客服助手。请友好、专业地回复用户消息。
                    
                    用户消息：%s
                    识别意图：%s
                    """.formatted(message, intent);
        }

        ChatResponse response = chatClient.prompt()
                .user(prompt)
                .call()
                .chatResponse();

        String reply = response.getResult().getOutput().getText();
        log.debug("回复生成完成, replyLength={}", reply != null ? reply.length() : 0);
        return reply;
    }

    private String parseIntent(String content) {
        try {
            int start = content.indexOf("\"intent\"");
            if (start == -1) return "unknown";
            int colonIdx = content.indexOf(":", start);
            int quoteStart = content.indexOf("\"", colonIdx + 1);
            int quoteEnd = content.indexOf("\"", quoteStart + 1);
            return content.substring(quoteStart + 1, quoteEnd);
        } catch (Exception e) {
            log.warn("意图解析失败, content={}", content, e);
            return "unknown";
        }
    }

    private Double parseConfidence(String content) {
        try {
            int start = content.indexOf("\"confidence\"");
            if (start == -1) return 0.5;
            int colonIdx = content.indexOf(":", start);
            int end = content.indexOf("}", colonIdx);
            String numStr = content.substring(colonIdx + 1, end).trim().replace(",", "");
            return Double.parseDouble(numStr);
        } catch (Exception e) {
            log.warn("置信度解析失败, content={}", content, e);
            return 0.5;
        }
    }
}
