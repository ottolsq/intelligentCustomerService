package com.ics.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置
 * 定义项目使用的消息队列、交换机和绑定关系
 */
@Configuration
public class RabbitmqConfig {

    // ==================== 消息接收队列 ====================

    /**
     * 消息接收交换机
     * 生产者：conversation-service
     * 消费者：rule-engine, ai-service, analytics
     */
    @Bean
    public DirectExchange conversationMessageExchange() {
        return ExchangeBuilder.directExchange("conversation.message.exchange").durable(true).build();
    }

    @Bean
    public Queue conversationMessageReceivedQueue() {
        return QueueBuilder.durable("conversation.message.received").build();
    }

    @Bean
    public Binding bindConversationMessageReceived() {
        return BindingBuilder.bind(conversationMessageReceivedQueue())
                .to(conversationMessageExchange())
                .with("conversation.message.received");
    }

    // ==================== AI 响应队列 ====================

    /**
     * AI 响应交换机
     * 生产者：ai-service
     * 消费者：conversation-service, analytics
     */
    @Bean
    public DirectExchange conversationAiResponseExchange() {
        return ExchangeBuilder.directExchange("conversation.ai.response.exchange").durable(true).build();
    }

    @Bean
    public Queue conversationAiResponseQueue() {
        return QueueBuilder.durable("conversation.ai.response").build();
    }

    @Bean
    public Binding bindConversationAiResponse() {
        return BindingBuilder.bind(conversationAiResponseQueue())
                .to(conversationAiResponseExchange())
                .with("conversation.ai.response");
    }

    // ==================== 转人工队列 ====================

    /**
     * 转人工交换机
     * 生产者：rule-engine / ai-service
     * 消费者：conversation-service
     */
    @Bean
    public DirectExchange conversationAgentEscalationExchange() {
        return ExchangeBuilder.directExchange("conversation.agent.escalation.exchange").durable(true).build();
    }

    @Bean
    public Queue conversationAgentEscalationQueue() {
        return QueueBuilder.durable("conversation.agent.escalation").build();
    }

    @Bean
    public Binding bindConversationAgentEscalation() {
        return BindingBuilder.bind(conversationAgentEscalationQueue())
                .to(conversationAgentEscalationExchange())
                .with("conversation.agent.escalation");
    }

    // ==================== 租户计费事件 ====================

    /**
     * 租户计费事件交换机
     * 生产者：各服务
     * 消费者：tenant-service, analytics
     */
    @Bean
    public DirectExchange tenantBillingEventExchange() {
        return ExchangeBuilder.directExchange("tenant.billing.event.exchange").durable(true).build();
    }

    @Bean
    public Queue tenantBillingEventQueue() {
        return QueueBuilder.durable("tenant.billing.event").build();
    }

    @Bean
    public Binding bindTenantBillingEvent() {
        return BindingBuilder.bind(tenantBillingEventQueue())
                .to(tenantBillingEventExchange())
                .with("tenant.billing.event");
    }
}
