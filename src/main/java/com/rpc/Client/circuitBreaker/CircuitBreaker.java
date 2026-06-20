package com.rpc.Client.circuitBreaker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CircuitBreaker {

    private CircuitBreakerState state=CircuitBreakerState.CLOSED;
    //计数器
    private AtomicInteger successCount=new AtomicInteger(0);
    private AtomicInteger failCount=new AtomicInteger(0);
    private AtomicInteger requestCount=new AtomicInteger(0);

    private final int failureThreshold;        // 失败次数阈值 → 触发熔断
    private final double halfOpenSuccessRate;  // 半开 → 关闭 的成功率阈值
    private final long retryTimePeriod;        // 冷却时间（OPEN 状态持续多久）

    // 最后一次失败的时间
    private long lastFailureTime = 0;

    public CircuitBreaker(int failureThreshold,
                          double halfOpenSuccessRate,
                          long retryTimePeriod) {
        this.failureThreshold = failureThreshold;
        this.halfOpenSuccessRate = halfOpenSuccessRate;
        this.retryTimePeriod = retryTimePeriod;
    }

    private void resetCounts() {
        failCount.set(0);
        successCount.set(0);
        requestCount.set(0);
    }

    public synchronized boolean allowRequest(){
        long currentTime=System.currentTimeMillis();
        switch (state){
            case OPEN:
                if(currentTime-lastFailureTime>retryTimePeriod){
                    state=CircuitBreakerState.HALF_OPEN;
                    resetCounts();
                    return true;
                }
                log.info("熔断器生效");
                return false;
            case HALF_OPEN:
                requestCount.incrementAndGet();
                return true;
            case CLOSED:
                return true;
            default:
                return true;
        }
    }

    public synchronized void recordSuccess(){
        if(state==CircuitBreakerState.HALF_OPEN){
            successCount.incrementAndGet();
            if(successCount.get()>=halfOpenSuccessRate*requestCount.get()){
                state=CircuitBreakerState.CLOSED;
                resetCounts();
            }
        }else{
            resetCounts();
        }
    }

    public synchronized void recordFail(){
        failCount.incrementAndGet();
        log.info("记录失败次数：{}",failCount.get());
        lastFailureTime=System.currentTimeMillis();
        if(state==CircuitBreakerState.HALF_OPEN){
            state=CircuitBreakerState.OPEN;
            lastFailureTime=System.currentTimeMillis();
        }else if(failCount.get()>=failureThreshold){
            state=CircuitBreakerState.OPEN;
        }
    }
}
