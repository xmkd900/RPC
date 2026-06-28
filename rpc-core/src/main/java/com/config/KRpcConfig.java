package com.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KRpcConfig {
    private String name = "krpc";           // 应用名
    private Integer port = 9999;            // 端口
    private String host = "localhost";      // 主机
    private String version = "1.0.0";      // 版本号
    private String registry = "ZKServiceRegister";  // 注册中心类型
    private String serializer = "Hessian";  // 序列化方式
    private String loadBalance = "ConsistencyHash";  // 负载均衡策略
}
