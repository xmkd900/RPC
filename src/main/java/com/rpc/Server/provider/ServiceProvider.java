package com.rpc.Server.provider;

import com.rpc.Server.serviceRegister.ServiceRegister;
import com.rpc.Server.serviceRegister.impl.ZKServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServiceProvider {
    //存放服务的实例
    private Map<String, Object> interfaceProvider;
    private String host;
    private int port;
    private ServiceRegister serviceRegister;

    public ServiceProvider(String host,int port){
        this.host=host;
        this.port=port;
        this.interfaceProvider=new HashMap<>();
        this.serviceRegister=new ZKServiceRegister();
    }

    public void provideServiceInterface(Object service ) {
        String interfaceName = service.getClass().getName();
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class<?> i : interfaces) {
            interfaceProvider.put(i.getName(), service);
            serviceRegister.register(i.getName(), new InetSocketAddress(host, port));
        }
    }

    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }

}
