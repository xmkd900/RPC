package com.rpc.Client.netty.serviceCenter.balance;

import java.util.List;

public interface LoadBalace {

    // 核心方法：从地址列表中选一个
    String balance(List<String> addressList) throws Exception;

    // 动态新增节点
    void addNode(String node);

    // 动态删除节点
    void delNode(String node);
}
