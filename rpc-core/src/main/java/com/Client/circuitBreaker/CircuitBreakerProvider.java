package com.Client.circuitBreaker;

import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CircuitBreakerProvider {
    Map<String,CircuitBreaker> circuitBreakerMap=new ConcurrentHashMap<>();

    public  CircuitBreaker getCircuitBreaker(String serviceName){
        return circuitBreakerMap.computeIfAbsent(serviceName, key -> {
            log.info("服务 [{}] 不存在熔断器，创建新的熔断器实例", serviceName);
            // 创建并返回新熔断器
            return new CircuitBreaker(1, 0.5, 10000);
        });
    }
}
