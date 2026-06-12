package com.rpc.Server.server.netty.nettyInitializer;

import com.rpc.Common.serializer.myCode.MyDecoder;
import com.rpc.Common.serializer.myCode.MyEncoder;
import com.rpc.Common.serializer.mySerializer.JsonSerializer;
import com.rpc.Server.provider.ServiceProvider;
import com.rpc.Server.server.netty.handler.NettyRPCServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private ServiceProvider serviceProvider;
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new NettyRPCServerHandler(serviceProvider));
    }
}
