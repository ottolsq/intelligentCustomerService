-- ==========================================
-- 智能客服 SaaS 平台 - AI 服务数据库初始化
-- ==========================================

CREATE DATABASE IF NOT EXISTS ics_ai DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ics_ai;

-- ==========================================
-- 1. 提示词模板表
-- ==========================================
DROP TABLE IF EXISTS `ai_prompt_template`;
CREATE TABLE `ai_prompt_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户 ID',
    `template_code` VARCHAR(128) NOT NULL COMMENT '模板编码（唯一标识）',
    `template_name` VARCHAR(256) NOT NULL COMMENT '模板名称',
    `content` TEXT NOT NULL COMMENT '模板内容（支持占位符 {variable}）',
    `category` VARCHAR(64) DEFAULT NULL COMMENT '用途分类: intent_classification/response_generation/faq_answer/escalation',
    `model_name` VARCHAR(64) DEFAULT NULL COMMENT '模型名称（如 gpt-4o-mini, gpt-4o）',
    `temperature` DOUBLE DEFAULT 0.7 COMMENT '温度参数',
    `max_tokens` INT DEFAULT 1024 COMMENT '最大 Token 数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0=禁用, 1=启用',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号',
    `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT 0 COMMENT '创建人',
    `update_by` BIGINT DEFAULT 0 COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_template_code` (`tenant_id`, `template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词模板表';

-- ==========================================
-- 2. AI 使用日志表
-- ==========================================
DROP TABLE IF EXISTS `ai_usage_log`;
CREATE TABLE `ai_usage_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户 ID',
    `conversation_id` BIGINT DEFAULT NULL COMMENT '会话 ID',
    `user_message` TEXT COMMENT '用户消息内容',
    `ai_response` TEXT COMMENT 'AI 回复内容',
    `model` VARCHAR(64) DEFAULT NULL COMMENT '使用的模型',
    `intent` VARCHAR(128) DEFAULT NULL COMMENT '意图分类结果',
    `confidence` DOUBLE DEFAULT NULL COMMENT '置信度 (0-1)',
    `prompt_tokens` INT DEFAULT NULL COMMENT '请求 Token 数',
    `completion_tokens` INT DEFAULT NULL COMMENT '响应 Token 数',
    `total_tokens` INT DEFAULT NULL COMMENT '总 Token 数',
    `latency_ms` BIGINT DEFAULT NULL COMMENT '耗时（毫秒）',
    `template_code` VARCHAR(128) DEFAULT NULL COMMENT '使用的提示词模板编码',
    `status` VARCHAR(32) DEFAULT 'success' COMMENT '状态: success/error/fallback',
    `error_message` TEXT COMMENT '错误信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT 0 COMMENT '创建人',
    `update_by` BIGINT DEFAULT 0 COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_conversation_id` (`conversation_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 使用日志表';

-- ==========================================
-- 3. 初始化提示词模板数据
-- ==========================================

-- 意图分类模板（tenant_id=1 默认租户）
INSERT INTO `ai_prompt_template` (`tenant_id`, `template_code`, `template_name`, `content`, `category`, `temperature`, `status`, `version`)
VALUES
    (1, 'intent_classification', '意图分类模板',
     '你是一个电商客服意图分类助手。请分析用户消息的意图，并从以下类别中选择一个：
- product_query: 商品咨询（价格、规格、库存、功能等）
- order_query: 订单查询（订单状态、物流、发货等）
- refund_return: 退换货咨询
- complaint: 投诉建议
- account_issue: 账户问题（登录、密码、会员等）
- promotion: 促销活动咨询
- general: 一般问题/闲聊
- unknown: 无法识别

请严格按照以下格式回复（不要添加其他内容）：
intent:<意图编码>, confidence:<0.0-1.0的置信度>

用户消息：{message}',
     'intent_classification', 0.1, 1, 1),

    (1, 'response_product_query', '商品咨询回复模板',
     '你是一个专业的电商客服。请根据用户的问题，提供友好、准确的回复。

参考信息：{context}

用户问题：{message}

请用中文回复，语气友好专业。如果涉及具体商品参数，请说明建议用户查看商品详情页。',
     'response_generation', 0.7, 1, 1),

    (1, 'response_order_query', '订单查询回复模板',
     '你是一个电商客服助手。用户正在咨询订单相关问题。

用户问题：{message}

请引导用户提供订单号，并说明你可以帮他们查询订单状态、物流信息等。语气友好专业。',
     'response_generation', 0.7, 1, 1),

    (1, 'response_refund_return', '退换货回复模板',
     '你是一个电商客服助手。用户正在咨询退换货问题。

用户问题：{message}

请说明退换货流程，并提醒用户注意退换货时效和商品状态要求。语气友好专业。',
     'response_generation', 0.7, 1, 1),

    (1, 'response_general', '通用回复模板',
     '你是一个智能客服助手。请根据用户的问题提供友好、准确的回复。

用户问题：{message}
参考信息：{context}

请用中文回复。',
     'response_generation', 0.7, 1, 1);
