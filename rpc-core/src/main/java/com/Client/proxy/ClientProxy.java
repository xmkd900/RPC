package com.Client.proxy;

import com.Client.circuitBreaker.CircuitBreaker;
import com.Client.circuitBreaker.CircuitBreakerProvider;
import com.Client.netty.rpcClient.Impl.NettyRpcClient;
import com.Client.netty.rpcClient.RpcClient;
import com.Client.netty.serviceCenter.ZKServiceCenter;
import com.common.Message.RPCrequest;
import com.common.Message.RPCresponse;
import com.Client.retry.GuavaRetry;
import com.trace.interceptor.ClientTraceInterceptor;

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
        ClientTraceInterceptor.beforeInvoke();
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
        ClientTraceInterceptor.afterInvoke(method.getName());
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

    public void close(){
        rpcClient.close();
        zkServiceCenter.close();
    }


    public <T> T getProxy(Class<T>clazz){
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                this
        );
    }
}

