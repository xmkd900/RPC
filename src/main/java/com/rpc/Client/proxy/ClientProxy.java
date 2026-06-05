package com.rpc.Client.proxy;

import com.rpc.Client.IOClient;
import com.rpc.Client.netty.rpcClient.Impl.NettyRpcClient;
import com.rpc.Client.netty.rpcClient.RpcClient;
import com.rpc.Common.Message.RPCrequest;
import com.rpc.Common.Message.RPCresponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientProxy implements InvocationHandler {
    private String host;
    private int port;
    private RpcClient rpcClient;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //封装rpcrequest请求
        RPCrequest rpcRequest=RPCrequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes())
                .build();
        RPCresponse response= rpcClient.sendRequest(rpcRequest);
        return response.getData();

    }


    public <T> T getProxy(Class<T>clazz){
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                this
        );
    }
}

