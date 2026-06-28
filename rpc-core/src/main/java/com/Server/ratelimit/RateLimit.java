package com.Server.ratelimit;

public interface RateLimit {
    // 获取访问许可（令牌）
    // 返回 true → 放行
    // 返回 false → 限流，拒绝请求
    boolean getToken();
}
