package com.rpc.Client.circuitBreaker;

public enum CircuitBreakerState {
    //关闭，开启，半开启
    CLOSED, OPEN, HALF_OPEN
}
