package com.Server.server;


import com.Server.provider.ServiceProvider;
import com.Server.server.impl.NettyRPCServer;
import com.Service.UserService;



//public class TestServer {
//    public static void main(String[] args) {
//        UserService userService = new UserServiceImpl();
//        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1",8888);
//        serviceProvider.provideServiceInterface(userService);
//        RpcServer rpcServer = new NettyRPCServer(serviceProvider);
//        rpcServer.start(8888);
//    }
//}
