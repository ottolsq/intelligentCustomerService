package com.ics.ai.controller;

import com.ics.ai.dto.AiChatRequest;
import com.ics.ai.dto.AiChatResponse;
import com.ics.ai.dto.IntentRequest;
import com.ics.ai.dto.IntentResponse;
import com.ics.ai.service.AiService;
import com.ics.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AI 对话控制器
 */
@Tag(name = "AI 对话", description = "智能回复、意图分类")
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @Operation(summary = "智能回复")
    @PostMapping("/chat")
    public Result<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request) {
        AiChatResponse response = aiService.chat(request);
        return Result.success(response);
    }

    @Operation(summary = "意图分类")
    @PostMapping("/intent")
    public Result<IntentResponse> classifyIntent(@Valid @RequestBody IntentRequest request) {
        IntentResponse response = aiService.classifyIntent(request);
        return Result.success(response);
    }
}
