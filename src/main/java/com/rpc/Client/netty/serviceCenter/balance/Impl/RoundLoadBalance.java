package com.rpc.Client.netty.serviceCenter.balance.Impl;

import com.rpc.Client.netty.serviceCenter.balance.LoadBalace;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RoundLoadBalance implements LoadBalace {

    //用AtomicInteger保证线程安全
    private AtomicInteger choose=new AtomicInteger(0);
    private List<String>addresslist=new CopyOnWriteArrayList<>();
    @Override
    public String balance(List<String> addressList) throws Exception {
        if(addressList==null||addressList.size()==0){
            throw new Exception("addressList is null");
        }
        int currentChoose = choose.getAndUpdate(i -> (i + 1) % addressList.size());
        String address=addressList.get(currentChoose);
        log.info("负载均衡选择了{}服务器",address);
        return address;
    }

    @Override
    public void addNode(String node) {
        if(node==null||node.length()==0){
            return ;
        }
        addresslist.add(node);
    }

    @Override
    public void delNode(String node) {
        if(node==null||node.length()==0){
            return ;
        }
        addresslist.remove(node);
    }
}
