package com.ics.gateway.filter;

import com.ics.common.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 基于 Redis 的限流过滤器
 * 按租户进行限流（滑动窗口计数器）
 */
@Slf4j
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    /**
     * 默认限流：10 请求/秒（免费版）
     */
    private static final int DEFAULT_RATE = 10;

    public RateLimitFilter(ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String tenantId = request.getHeaders().getFirst(CommonConstants.HEADER_TENANT_ID);

        if (tenantId == null || tenantId.isBlank()) {
            // 没有租户 ID，跳过限流
            return chain.filter(exchange);
        }

        String key = "ics:rate_limit:" + tenantId + ":" + System.currentTimeMillis() / 1000;

        return redisTemplate.opsForValue().increment(key, 1)
                .flatMap(count -> {
                    if (count != null && count.intValue() > DEFAULT_RATE) {
                        log.warn("Rate limit exceeded for tenant: {}", tenantId);
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        return exchange.getResponse().setComplete();
                    }
                    // 设置 2 秒过期，自动清理
                    redisTemplate.expire(key, Duration.ofSeconds(2)).subscribe();
                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return -50; // 在 JWT 过滤器之后执行
    }
}
