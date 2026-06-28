package com.Client.netty.handler;
import com.common.Message.RPCresponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;




public class NettyClientHandler extends SimpleChannelInboundHandler<RPCresponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RPCresponse  response) throws Exception {
        AttributeKey<RPCresponse> key = AttributeKey.valueOf("RPCResponse");
        channelHandlerContext.channel().attr(key).set(response);
        channelHandlerContext.channel().close();;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
