package com.rpc.Server.provider;

import java.util.HashMap;
import java.util.Map;

public class ServiceProvider {
    //存放服务的实例
    private Map<String, Object> interfaceProvider;

    public ServiceProvider(){
        this.interfaceProvider=new HashMap<>();
    }

    public void provideServiceInterface(Object service ) {
        String interfaceName = service.getClass().getName();
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class<?> i : interfaces) {
            interfaceProvider.put(i.getName(), service);
        }
    }

    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }

}
