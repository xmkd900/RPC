package com.rpc.Server.server.impl;

import com.rpc.Server.provider.ServiceProvider;
import com.rpc.Server.server.RpcServer;
import com.rpc.Server.server.netty.nettyInitializer.NettyServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class NettyRPCRPCServer implements RpcServer {
    private ServiceProvider serviceProvider;
    @Override
    public void start(int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        log.info("netty 服务器启动了");
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();//启动器
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)  //指定通道(channel)类型
                    .childHandler(new NettyServerInitializer(serviceProvider));//handler
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();//绑定端口
            channelFuture.channel().closeFuture().sync(); //让服务器保持运行
        }catch(Exception e){
            log.error("netty服务器启动失败",e);
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }
}
