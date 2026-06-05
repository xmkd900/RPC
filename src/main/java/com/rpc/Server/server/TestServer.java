package com.rpc.Server.server;

import com.rpc.Common.Service.UserService;
import com.rpc.Common.Service.Impl.UserServiceImpl;
import com.rpc.Server.provider.ServiceProvider;
import com.rpc.Server.server.impl.NettyRPCRPCServer;
import com.rpc.Server.server.impl.ThreadPoolRPCRPCServer;


public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);
        RpcServer rpcServer = new NettyRPCRPCServer(serviceProvider);
        rpcServer.start(8888);
    }
}
