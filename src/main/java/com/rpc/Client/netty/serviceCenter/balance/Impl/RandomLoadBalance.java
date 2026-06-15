package com.rpc.Client.netty.serviceCenter.balance.Impl;

import com.rpc.Client.netty.serviceCenter.balance.LoadBalace;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class RandomLoadBalance implements LoadBalace {

    private final  Random random=new Random();
    private final List<String> addressList = new CopyOnWriteArrayList<>();
    @Override
    public String balance(List<String> addressList) throws Exception {
        if(addressList==null||addressList.size()==0){
            throw new Exception("没有可用的服务器");
        }
        int choose=random.nextInt(addressList.size());
        log.info("负载均衡选择了{}服务器",addressList.get(choose));
        return addressList.get(choose);
    }

    @Override
    public void addNode(String node) {
        if(node==null||node.length()==0){
            return;
        }
        addressList.add(node);
        log.info("节点 {} 已加入负载均衡", node);
    }

    @Override
    public void delNode(String node) {
        if(node==null||node.length()==0){
            return;
        }
        addressList.remove(node);
        log.info("节点 {} 已从负载均衡中移除", node);

    }
}
