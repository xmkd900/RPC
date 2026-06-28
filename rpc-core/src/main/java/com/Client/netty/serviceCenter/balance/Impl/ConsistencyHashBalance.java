package com.Client.netty.serviceCenter.balance.Impl;

import com.Client.netty.serviceCenter.balance.LoadBalace;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ConsistencyHashBalance implements LoadBalace {
    //虚拟节点数
    private static final int VIRTUAL_NUM=5;
    //虚拟节点，key是hash值，value是节点服务器地址
    private SortedMap<Integer,String>shards=new TreeMap<>();
    // 真实节点列表
    private List<String> realNodes = new LinkedList<>();
    // 模拟初始服务器
    private String[] servers = null;

    private void init(List<String>serviceList){
        for(String server:serviceList){
            realNodes.add(server);
            log.info("真实节点{}被添加",server);
            for(int i=0;i<VIRTUAL_NUM;i++){
                String virtualNode=server+"&&VN"+i;
                int hash=gethash(virtualNode);
                shards.put(hash,virtualNode);
                log.info("虚拟节点{}被添加",virtualNode);
            }

        }
    }

    public String getServer(String node,List<String> serviceList){
        init(serviceList);
        int hash=gethash(node);
        Integer key=null;
        SortedMap<Integer,String>subMap=shards.tailMap(hash);
        if(subMap.isEmpty()){
            key=shards.firstKey();
        }else{
            key=subMap.firstKey();
        }
        String address=shards.get(key);
        return address.substring(0,address.indexOf("&&"));
    }

    private int gethash(String str) {
        final int p = 16777619;                          // FNV 质数
        int hash = (int) 2166136261L;                    // FNV 偏移基数
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;            // ① 异或 + 乘质数
        hash += hash << 13;                               // ② 额外混淆
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        return hash & 0x7FFFFFFF; //确保非负
    }


    @Override
    public String balance(List<String> addressList) throws Exception {
        String random= UUID.randomUUID().toString();
        return getServer(random,addressList);
    }

    @Override
    public void addNode(String node) {
        if(!realNodes.contains(node)){
            realNodes.add(node);
            log.info("真实节点{}被添加",node);
            for(int i=0;i<VIRTUAL_NUM;i++){
                String virtualNode=node+"&&VN"+i;
                int hash=gethash(virtualNode);
                shards.put(hash,virtualNode);
                log.info("虚拟节点{}被添加",virtualNode);
            }
        }

    }

    @Override
    public void delNode(String node) {
        if(realNodes.contains(node)){
            realNodes.remove(node);
            log.info("真实节点{}被删除",node);
            for(int i=0;i<VIRTUAL_NUM;i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = gethash(virtualNode);
                shards.remove(hash);
                log.info("虚拟节点{}被删除", virtualNode);
            }
        }

    }
}
