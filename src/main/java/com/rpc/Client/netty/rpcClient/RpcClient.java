package com.rpc.Client.netty.rpcClient;

import com.rpc.Common.Message.RPCrequest;
import com.rpc.Common.Message.RPCresponse;

public interface RpcClient {
    RPCresponse sendRequest(RPCrequest request);
}
