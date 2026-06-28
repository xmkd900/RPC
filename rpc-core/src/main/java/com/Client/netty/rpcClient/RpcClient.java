package com.Client.netty.rpcClient;


import com.common.Message.RPCrequest;
import com.common.Message.RPCresponse;

public interface RpcClient {
    RPCresponse sendRequest(RPCrequest request);

    void close();
}
