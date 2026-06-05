package com.rpc.Client.netty.rpcClient.Impl;

import com.rpc.Client.netty.rpcClient.RpcClient;
import com.rpc.Common.Message.RPCrequest;
import com.rpc.Common.Message.RPCresponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.jar.Attributes;


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
        InetSocketAddress address=serviceCenter.servicerDiscovery(request.getInterfaceName());
        String host=address.getHostName();
        int port=address.getPort();
        try{
            //连接服务器
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel=channelFuture.channel();  //获取channel
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
            AttributeKey<RPCresponse> key=AttributeKey.valueOf("RPCresponse");
            RPCresponse response=channel.attr(key).get();
            System.out.println(response);
            return response;

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
