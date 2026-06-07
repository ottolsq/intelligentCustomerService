package com.ics.ai.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ics.ai.dto.PromptTemplateRequest;
import com.ics.ai.entity.PromptTemplate;
import com.ics.ai.mapper.PromptTemplateMapper;
import com.ics.ai.service.PromptTemplateService;
import com.ics.common.enums.ResultCode;
import com.ics.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提示词模板服务实现
 */
@Service
@RequiredArgsConstructor
public class PromptTemplateServiceImpl implements PromptTemplateService {

    private final PromptTemplateMapper promptTemplateMapper;

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}");

    @Override
    @Transactional
    public PromptTemplate create(PromptTemplateRequest request) {
        // 检查编码是否已存在
        PromptTemplate existing = getByCode(request.getTemplateCode());
        if (existing != null) {
            throw new BusinessException(ResultCode.CONFLICT, "模板编码已存在: " + request.getTemplateCode());
        }

        PromptTemplate template = new PromptTemplate();
        template.setTemplateCode(request.getTemplateCode());
        template.setTemplateName(request.getTemplateName());
        template.setContent(request.getContent());
        template.setCategory(request.getCategory());
        template.setModelName(request.getModelName());
        template.setTemperature(request.getTemperature());
        template.setMaxTokens(request.getMaxTokens());
        template.setStatus(request.getStatus());
        template.setVersion(1);
        template.setRemark(request.getRemark());

        promptTemplateMapper.insert(template);
        return template;
    }

    @Override
    @Transactional
    public PromptTemplate update(Long id, PromptTemplateRequest request) {
        PromptTemplate template = promptTemplateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "模板不存在: id=" + id);
        }

        template.setTemplateName(request.getTemplateName());
        template.setContent(request.getContent());
        template.setCategory(request.getCategory());
        template.setModelName(request.getModelName());
        template.setTemperature(request.getTemperature());
        template.setMaxTokens(request.getMaxTokens());
        template.setStatus(request.getStatus());
        template.setVersion(template.getVersion() + 1);
        template.setRemark(request.getRemark());

        promptTemplateMapper.updateById(template);
        return template;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        promptTemplateMapper.deleteById(id);
    }

    @Override
    public PromptTemplate getById(Long id) {
        PromptTemplate template = promptTemplateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "模板不存在: id=" + id);
        }
        return template;
    }

    @Override
    public PromptTemplate getByCode(String templateCode) {
        return promptTemplateMapper.selectOne(
                Wrappers.<PromptTemplate>lambdaQuery()
                        .eq(PromptTemplate::getTemplateCode, templateCode)
                        .eq(PromptTemplate::getStatus, 1)
                        .orderByDesc(PromptTemplate::getVersion)
                        .last("LIMIT 1")
        );
    }

    @Override
    public String render(String templateCode, Map<String, String> variables) {
        PromptTemplate template = getByCode(templateCode);
        if (template == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "模板不存在: " + templateCode);
        }

        String content = template.getContent();
        Matcher matcher = VARIABLE_PATTERN.matcher(content);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String varName = matcher.group(1);
            String replacement = variables.getOrDefault(varName, "");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}
