package com.ics.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 提示词模板创建/更新请求
 */
@Data
public class PromptTemplateRequest {

    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @NotBlank(message = "模板内容不能为空")
    private String content;

    private String category;
    private String modelName;
    private Double temperature = 0.7;
    private Integer maxTokens = 1024;
    private Integer status = 1;
    private String remark;
}
