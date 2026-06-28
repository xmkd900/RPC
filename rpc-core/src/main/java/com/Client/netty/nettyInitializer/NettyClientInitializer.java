package com.Client.netty.nettyInitializer;
import com.Client.netty.handler.HeartbeatHandler;
import com.Client.netty.handler.MDCChannelHandler;
import com.Client.netty.handler.NettyClientHandler;
import com.common.serializer.myCode.MyDecoder;
import com.common.serializer.myCode.MyEncoder;
import com.common.serializer.mySerializer.JsonSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.util.concurrent.TimeUnit;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new NettyClientHandler());
        pipeline.addLast(new MDCChannelHandler());
        pipeline.addLast(new IdleStateHandler(0, 8, 0, TimeUnit.SECONDS));
        pipeline.addLast(new HeartbeatHandler());
    }
}
