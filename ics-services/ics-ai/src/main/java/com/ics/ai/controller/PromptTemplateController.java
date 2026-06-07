package com.ics.ai.controller;

import com.ics.ai.dto.PromptTemplateRequest;
import com.ics.ai.entity.PromptTemplate;
import com.ics.ai.service.PromptTemplateService;
import com.ics.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 提示词模板控制器
 */
@Tag(name = "提示词模板管理", description = "Prompt 模板 CRUD")
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptTemplateController {

    private final PromptTemplateService promptTemplateService;

    @Operation(summary = "创建模板")
    @PostMapping
    public Result<PromptTemplate> create(@Valid @RequestBody PromptTemplateRequest request) {
        PromptTemplate template = promptTemplateService.create(request);
        return Result.success(template);
    }

    @Operation(summary = "更新模板")
    @PutMapping("/{id}")
    public Result<PromptTemplate> update(@PathVariable Long id, @Valid @RequestBody PromptTemplateRequest request) {
        PromptTemplate template = promptTemplateService.update(id, request);
        return Result.success(template);
    }

    @Operation(summary = "删除模板")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        promptTemplateService.delete(id);
        return Result.success();
    }

    @Operation(summary = "查询模板详情")
    @GetMapping("/{id}")
    public Result<PromptTemplate> getById(@PathVariable Long id) {
        PromptTemplate template = promptTemplateService.getById(id);
        return Result.success(template);
    }

    @Operation(summary = "根据编码查询模板")
    @GetMapping("/code/{code}")
    public Result<PromptTemplate> getByCode(@PathVariable String code) {
        PromptTemplate template = promptTemplateService.getByCode(code);
        return Result.success(template);
    }
}
