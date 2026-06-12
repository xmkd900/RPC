package com.rpc.Client.netty.serviceCenter;

import com.rpc.Client.cache.ServiceCache;
import com.rpc.Client.netty.serviceCenter.ZkWatcher.watchZK;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZKServiceCenter implements ServiceCenter{

    private CuratorFramework client;
    private static final String ROOT_PATH = "MYRPC";
    private ServiceCache serviceCache;
    public ZKServiceCenter() {
        RetryPolicy retryPolicy= new ExponentialBackoffRetry(1000,3);
        this.client=CuratorFrameworkFactory.builder()
                .namespace(ROOT_PATH)
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(4000)
                .retryPolicy(retryPolicy)
                .build();
        this.serviceCache=new ServiceCache();
        this.client.start();
        watchZK watcher=new watchZK(client,serviceCache);
        watcher.watchToUpdate(ROOT_PATH);
        log.info("zookeeper client start successfully!");
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
          List<String>serviceList=serviceCache.getServiceListFromCache(serviceName);
            log.info("缓存中的服务列表：{}", serviceList);
          if(serviceList==null||serviceList.size()==0){
              serviceList=client.getChildren().forPath("/"+serviceName);
              log.info("从ZK获取到子节点：{}", serviceList);  // 新增
          }
            String address = serviceList.get(0);
            return parseAddress(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private InetSocketAddress parseAddress(String address) {
        String []strings=address.split(":");
        return new InetSocketAddress(strings[0],Integer.parseInt(strings[1]));

    }
}
