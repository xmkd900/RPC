package com.rpc.Client.netty.rpcClient.Impl;

import com.rpc.Client.netty.nettyInitializer.NettyClientInitializer;
import com.rpc.Client.netty.rpcClient.RpcClient;
import com.rpc.Client.netty.serviceCenter.ServiceCenter;
import com.rpc.Client.netty.serviceCenter.ZKServiceCenter;
import com.rpc.Common.Message.RPCrequest;
import com.rpc.Common.Message.RPCresponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.jar.Attributes;


@Slf4j
public class NettyRpcClient implements RpcClient {

    private static final Bootstrap bootstrap;
    private static  final EventLoopGroup eventLoopGroup;
    private ServiceCenter serviceCenter;
    public NettyRpcClient (){
        this.serviceCenter=new ZKServiceCenter();
    }

    static{
        bootstrap=new Bootstrap();
        eventLoopGroup=new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }
    @Override
    public RPCresponse sendRequest(RPCrequest request) {
        InetSocketAddress address=serviceCenter.serviceDiscovery(request.getInterfaceName());
        String host=address.getHostName();
        int port=address.getPort();
        try{
            //连接服务器
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            log.info("连接成功：{}:{}", host, port);
            Channel channel=channelFuture.channel();  //获取channel
            channel.writeAndFlush(request);
            log.info("请求已发送");
            channel.closeFuture().sync();
            log.info("通道已关闭");
            AttributeKey<RPCresponse> key=AttributeKey.valueOf("RPCResponse");
            RPCresponse response=channel.attr(key).get();
            log.info("获取到的响应：{}", response);
            System.out.println(response);
            return response;

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
