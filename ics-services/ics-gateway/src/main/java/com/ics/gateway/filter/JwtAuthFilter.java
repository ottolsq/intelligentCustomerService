package com.ics.gateway.filter;

import com.ics.common.constants.CommonConstants;
import com.ics.security.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * JWT 认证过滤器
 * 验证请求中的 JWT Token，提取用户信息并传递给下游服务
 */
@Slf4j
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * 不需要认证的路径白名单
     */
    private static final List<String> WHITE_LIST = List.of(
            "/api/v1/auth/login",
            "/swagger-ui.html",
            "/swagger-ui/",
            "/v3/api-docs",
            "/actuator/health"
    );

    public JwtAuthFilter(ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 白名单路径跳过认证
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        // 获取 Authorization Header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        // 验证 Token 有效性
        if (!isValidToken(token)) {
            log.warn("Invalid or expired token for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 检查 Token 是否在黑名单中（已注销）
        String blacklistKey = CommonConstants.REDIS_TOKEN_BLACKLIST_PREFIX + token.hashCode();
        return redisTemplate.hasKey(blacklistKey)
                .flatMap(isBlacklisted -> {
                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        log.warn("Token is blacklisted for path: {}", path);
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    // 从 Token 中提取用户信息
                    try {
                        Claims claims = JwtUtil.parseToken(jwtSecret, token);
                        String userId = claims.getSubject();
                        String tenantId = claims.get(CommonConstants.JWT_CLAIM_TENANT_ID, String.class);

                        // 将用户信息传递给下游服务
                        ServerHttpRequest mutatedRequest = request.mutate()
                                .header(CommonConstants.HEADER_TENANT_ID, tenantId != null ? tenantId : "")
                                .header("X-User-Id", userId != null ? userId : "")
                                .build();

                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    } catch (Exception e) {
                        log.error("Failed to parse JWT token: {}", e.getMessage());
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                });
    }

    @Override
    public int getOrder() {
        return -100; // 高优先级，最先执行
    }

    private boolean isWhitelisted(String path) {
        return WHITE_LIST.stream().anyMatch(path::startsWith);
    }

    private boolean isValidToken(String token) {
        try {
            return JwtUtil.isValidToken(jwtSecret, token);
        } catch (Exception e) {
            return false;
        }
    }
}
