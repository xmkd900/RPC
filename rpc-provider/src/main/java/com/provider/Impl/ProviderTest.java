package com.provider.Impl;


import com.KRpcApplication;
import com.Server.provider.ServiceProvider;
import com.Server.server.RpcServer;
import com.Server.server.impl.NettyRPCServer;
import com.Service.UserService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ProviderTest {

    public static void main(String[] args) throws InterruptedException {
        KRpcApplication.initialize();
        String ip=KRpcApplication.getRpcConfig().getHost();
        int port=KRpcApplication.getRpcConfig().getPort();
        // 创建 UserService 实例
        UserService userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider(ip, port);
        // 发布服务接口到 ServiceProvider
        serviceProvider.provideServiceInterface(userService);  // 可以设置是否支持重试

        // 启动 RPC 服务器并监听端口
        RpcServer rpcServer = new NettyRPCServer(serviceProvider);
        rpcServer.start(port);  // 启动 Netty RPC 服务，监听 port 端口
        log.info("RPC 服务端启动，监听端口" + port);
    }

}
