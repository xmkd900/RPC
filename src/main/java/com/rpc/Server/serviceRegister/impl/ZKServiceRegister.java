package com.rpc.Server.serviceRegister.impl;

import com.rpc.Server.serviceRegister.ServiceRegister;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;

@Slf4j
public class ZKServiceRegister implements ServiceRegister {

    //zookeeper客户端
    private CuratorFramework client;
    //注册路径
    private static final String ROOT_PATH = "MYRPC";

    public ZKServiceRegister() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(4000)
                .retryPolicy(retryPolicy)
                .namespace(ROOT_PATH)
                .build();
        this.client.start();
        log.info("zookeeper连接成功");
    }

    @Override
    public void register(String serviceName, InetSocketAddress serviceAddress) {
        try {
            if (client.checkExists().forPath("/" + serviceName) == null) {
                client.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath("/" + serviceName);
            }
            String path="/"+serviceName+"/"+getServiceAddress(serviceAddress);
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);

        } catch (Exception e) {
            log.info("该节点已存在");
            log.error("注册服务 {} 失败", serviceName, e);
        }
    }

    private String getServiceAddress(InetSocketAddress inetSocketAddress){
        return inetSocketAddress.getHostName()+":"+inetSocketAddress.getPort();
    }

    private InetSocketAddress parseServiceAddress(String address){
        String[]list=address.split(":");
        return new InetSocketAddress(list[0],Integer.parseInt(list[1]));
    }
}
