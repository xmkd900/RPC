package com.rpc.Client.proxy;

import com.rpc.Client.circuitBreaker.CircuitBreaker;
import com.rpc.Client.circuitBreaker.CircuitBreakerProvider;
import com.rpc.Client.netty.rpcClient.Impl.NettyRpcClient;
import com.rpc.Client.netty.rpcClient.RpcClient;
import com.rpc.Client.netty.serviceCenter.ZKServiceCenter;
import com.rpc.Client.retry.GuavaRetry;
import com.rpc.Common.Message.RPCrequest;
import com.rpc.Common.Message.RPCresponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class ClientProxy implements InvocationHandler {
    private RpcClient rpcClient;

    private ZKServiceCenter zkServiceCenter;

    private CircuitBreakerProvider circuitBreakerProvider;

    public ClientProxy(){
this.rpcClient=new NettyRpcClient();
this.zkServiceCenter=new ZKServiceCenter();
this.circuitBreakerProvider=new CircuitBreakerProvider();
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //封装rpcrequest请求
        RPCrequest rpcRequest=RPCrequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes())
                .build();
        CircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(method.getName());
        if(!circuitBreaker.allowRequest()){
            return null;
        }
        RPCresponse response;
        String methodSignature=getMethodSignature(rpcRequest.getInterfaceName(),method);
        if(zkServiceCenter.checkRetry(zkServiceCenter.serviceDiscovery(rpcRequest.getInterfaceName()),methodSignature)){
            response=new GuavaRetry(rpcClient).sendResponseWithRetry(rpcRequest,rpcClient);
        }else{
            response=rpcClient.sendRequest(rpcRequest);
        }
       if(response!=null){
            int code = response.getCode();
            if (code == 200) {
                circuitBreaker.recordSuccess();
            } else if (code == 500) {
                circuitBreaker.recordFail();
            }
        }
        return response!=null?response.getData():null;

    }

    private String getMethodSignature(String interfaceName, Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(interfaceName)
                .append("#")
                .append(method.getName())
                .append("(");
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            sb.append(parameterType.getName());
            if (i != parameterTypes.length - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }


    public <T> T getProxy(Class<T>clazz){
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                this
        );
    }
}

