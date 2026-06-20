package com.rpc.Server.server.netty.handler;

import com.rpc.Common.Message.RPCrequest;
import com.rpc.Common.Message.RPCresponse;
import com.rpc.Server.provider.ServiceProvider;
import com.rpc.Server.ratelimit.RateLimit;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@AllArgsConstructor
public class NettyRPCServerHandler extends SimpleChannelInboundHandler<RPCrequest> {

    private ServiceProvider serviceProvider;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RPCrequest rpCrequest) throws Exception {
        log.info("收到请求：interface={}, method={}", rpCrequest.getInterfaceName(), rpCrequest.getMethodName());
        RPCresponse response = getResponse(rpCrequest);
        channelHandlerContext.writeAndFlush(response);
        channelHandlerContext.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private RPCresponse getResponse(RPCrequest request){
        String interfaceName=request.getInterfaceName();
        RateLimit rateLimit=serviceProvider.getRateLimitProvider().getRateLimit(interfaceName);
        if(!rateLimit.getToken()){
            log.info("服务器限流，接口：{}",interfaceName);
            return RPCresponse.fail(500,"rate limit");
        }
        Object service = serviceProvider.getService(interfaceName);
        try {
            Method method=service.getClass().getMethod(request.getMethodName(),request.getParameterTypes());
           Object result= method.invoke(service,request.getParameters());
           return RPCresponse.success(result);
        } catch (Exception e) {
           e.printStackTrace();
           return RPCresponse.fail(500,e.getMessage());
        }
    }
}
