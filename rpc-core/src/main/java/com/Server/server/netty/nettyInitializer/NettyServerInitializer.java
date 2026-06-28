package com.Server.server.netty.nettyInitializer;


import com.Client.netty.handler.HeartbeatHandler;
import com.Server.provider.ServiceProvider;
import com.Server.server.netty.handler.NettyRPCServerHandler;
import com.common.serializer.myCode.MyDecoder;
import com.common.serializer.myCode.MyEncoder;
import com.common.serializer.mySerializer.JsonSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.AllArgsConstructor;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private ServiceProvider serviceProvider;
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 服务端关注读事件和写事件，如果10秒内没有收到客户端的消息，将会触发IdleState.READER_IDLE事件，将由HeartbeatHandler进行处理
        pipeline.addLast(new IdleStateHandler(10, 20, 0 , TimeUnit.SECONDS));
        pipeline.addLast(new HeartbeatHandler());
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new NettyRPCServerHandler(serviceProvider));
    }
}
