package com.rpc.Client.netty.nettyInitializer;

import com.rpc.Client.netty.handler.NettyClientHandler;
import com.rpc.Common.serializer.myCode.MyDecoder;
import com.rpc.Common.serializer.myCode.MyEncoder;
import com.rpc.Common.serializer.mySerializer.JsonSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new NettyClientHandler());
    }
}
