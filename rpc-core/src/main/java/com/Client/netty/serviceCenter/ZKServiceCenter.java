package com.Client.netty.serviceCenter;


import com.Client.cache.ServiceCache;
import com.Client.netty.serviceCenter.ZkWatcher.watchZK;
import com.Client.netty.serviceCenter.balance.Impl.ConsistencyHashBalance;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class ZKServiceCenter implements ServiceCenter{

    private CuratorFramework client;
    private static final String ROOT_PATH = "MYRPC";
    private static final String RETRY="CANRETRY";
    private ServiceCache serviceCache;
    private Set<String>retryCache=new CopyOnWriteArraySet<>();
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
            String address = new ConsistencyHashBalance().balance(serviceList);
            return parseAddress(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {
        client.close();
    }

    public boolean checkRetry(InetSocketAddress serviceAddress, String methodSignature) {
        if(retryCache.isEmpty()){
            try {
            CuratorFramework curatorFramework=client.usingNamespace(RETRY);
                List<String>retryableMethods=curatorFramework.getChildren().forPath("/"+getServiceAddress(serviceAddress));
                retryCache.addAll(retryableMethods);
            } catch (Exception e) {
                log.error("检查重试失败，方法签名：{}", methodSignature, e);
            }
        }
        return retryCache.contains(methodSignature);
    }

    private InetSocketAddress parseAddress(String address) {
        String []strings=address.split(":");
        return new InetSocketAddress(strings[0],Integer.parseInt(strings[1]));
    }

    private String getServiceAddress(InetSocketAddress inetSocketAddress){
        return inetSocketAddress.getAddress().getHostAddress()+":"+inetSocketAddress.getPort();
    }
}
