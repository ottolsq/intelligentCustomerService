package com.ics.platform.service;

import com.ics.platform.dto.LoginRequest;
import com.ics.platform.dto.LoginResponse;

/**
 * 认证服务
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含 JWT Token）
     */
    LoginResponse login(LoginRequest request);

    /**
     * 刷新 Token
     *
     * @param token 旧 Token
     * @return 新 Token
     */
    String refreshToken(String token);

    /**
     * 注销（将 Token 加入黑名单）
     *
     * @param token Token
     */
    void logout(String token);

    /**
     * 验证 Token 是否有效
     *
     * @param token Token
     * @return 是否有效
     */
    boolean validateToken(String token);
}
