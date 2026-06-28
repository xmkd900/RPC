package com.Server.ratelimit.Provider;
import com.Server.ratelimit.Impl.TokenBucketRateLimitImpl;
import com.Server.ratelimit.RateLimit;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RateLimitProvider {

    private final Map<String, RateLimit>rateLimitMap=new ConcurrentHashMap<>();
    private static final int DEFAULT_CAPACITY = 10;
    private static final int DEFAULT_RATE = 100;
    public RateLimit getRateLimit(String interfaceName){
        return rateLimitMap.computeIfAbsent(interfaceName, key -> {
            RateLimit rateLimit = new TokenBucketRateLimitImpl(DEFAULT_RATE, DEFAULT_CAPACITY);
            log.info("为接口 [{}] 创建了新的限流策略: {}", interfaceName, rateLimit);
            return rateLimit;
        });
    }
}
