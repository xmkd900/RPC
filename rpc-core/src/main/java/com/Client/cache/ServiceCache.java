package com.Client.cache;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ServiceCache {
    private static Map<String, List<String>>cache=new HashMap<>();

    public  void addServiceToCache(String serviceName,String address){
        if(cache.containsKey(serviceName)){
            List<String>services=cache.get(serviceName);
            services.add(address);
        }else{
            List<String>services=new ArrayList<>();
            services.add(address);
            cache.put(serviceName,services);
        }
    }

    //修改服务地址
    public void replaceServiceAddress(String serviceName,String oldName, String newName) {
        if (cache.containsKey(serviceName)) {
            List<String> services = cache.get(serviceName);
            services.remove(oldName);
            services.add(newName);
        } else {
            log.info("该服务不存在");

        }
    }

    // 从缓存中取服务地址列表
    public List<String> getServiceListFromCache(String serviceName) {
        if (!cache.containsKey(serviceName)) {
            return null;
        }
        return cache.get(serviceName);
    }

    // 从缓存中删除服务地址
    public void delete(String serviceName, String address) {
        List<String> addressList = cache.get(serviceName);
        addressList.remove(address);
        System.out.println("将name为" + serviceName + "和地址为" + address + "的服务从本地缓存中删除");
    }
}
