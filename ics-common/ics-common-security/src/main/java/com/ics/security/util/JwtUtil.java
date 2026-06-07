package com.ics.security.util;

import com.ics.common.constants.CommonConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 */
public final class JwtUtil {

    private JwtUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 默认过期时间：24 小时
     */
    private static final long DEFAULT_EXPIRATION_MS = 24 * 60 * 60 * 1000L;

    /**
     * 生成 JWT Token
     *
     * @param secret     签名密钥
     * @param subject    主题（通常为 userId）
     * @param claims     自定义 claims
     * @param expiration 过期时间（毫秒）
     * @return JWT 字符串
     */
    public static String generateToken(String secret, String subject, Map<String, Object> claims, long expiration) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 生成 JWT Token（使用默认过期时间 24h）
     */
    public static String generateToken(String secret, String subject, Map<String, Object> claims) {
        return generateToken(secret, subject, claims, DEFAULT_EXPIRATION_MS);
    }

    /**
     * 解析并验证 Token
     *
     * @param secret 签名密钥
     * @param token  Token 字符串
     * @return Claims 对象
     */
    public static Claims parseToken(String secret, String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中获取 subject
     */
    public static String getSubject(String secret, String token) {
        return parseToken(secret, token).getSubject();
    }

    /**
     * 从 Token 中获取指定 claim
     */
    public static <T> T getClaim(String secret, String token, String claimKey) {
        Claims claims = parseToken(secret, token);
        return claims.get(claimKey, castClaimClass(claimKey));
    }

    /**
     * 判断 Token 是否过期
     */
    public static boolean isTokenExpired(String secret, String token) {
        Claims claims = parseToken(secret, token);
        return claims.getExpiration().before(new Date());
    }

    /**
     * 判断 Token 是否有效
     */
    public static boolean isValidToken(String secret, String token) {
        try {
            parseToken(secret, token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> castClaimClass(String claimKey) {
        return switch (claimKey) {
            case CommonConstants.JWT_CLAIM_TENANT_ID -> (Class<T>) String.class;
            case CommonConstants.JWT_CLAIM_USER_ID -> (Class<T>) String.class;
            case CommonConstants.JWT_CLAIM_ROLES -> (Class<T>) java.util.List.class;
            case CommonConstants.JWT_CLAIM_PERMISSIONS -> (Class<T>) java.util.List.class;
            default -> (Class<T>) Object.class;
        };
    }
}
