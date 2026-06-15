package com.rpc.Server.serviceRegister.impl;

import com.rpc.Common.annotation.Retryable;
import com.rpc.Server.serviceRegister.ServiceRegister;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ZKServiceRegister implements ServiceRegister {

    private static final String RETRY = "CANRETRY";
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
    public void register(Class<?>clazz, InetSocketAddress serviceAddress) {
        String serviceName = clazz.getName();
        try {
            if (client.checkExists().forPath("/" + serviceName) == null) {
                client.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath("/" + serviceName);
            }
            String path="/"+serviceName+"/"+getServiceAddress(serviceAddress);
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
            List<String> retryableMethods = getRetryableMethod(clazz);
            log.info("可重试的方法有{}",retryableMethods);
            CuratorFramework retryClient = client.usingNamespace(RETRY);
            for(String method:retryableMethods){
                retryClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                        .forPath("/"+getServiceAddress(serviceAddress)+"/"+method);
            }

        } catch (Exception e) {
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

    private List<String> getRetryableMethod(Class<?> clazz){
        List<String> retryableMethods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Retryable.class)) {
                String methodSignature = getMethodSignature(clazz, method);
                retryableMethods.add(methodSignature);
            }
        }
        return retryableMethods;
    }

    private String getMethodSignature(Class<?> clazz, Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(clazz.getName()).append("#").append(method.getName()).append("(");
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            sb.append(parameterTypes[i].getName());
            if (i < parameterTypes.length - 1) {
                sb.append(",");
            } else{
                sb.append(")");
            }
        }
        return sb.toString();
    }
}
