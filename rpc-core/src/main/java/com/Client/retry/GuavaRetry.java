package com.Client.retry;

import com.Client.netty.rpcClient.RpcClient;
import com.common.Message.RPCrequest;
import com.common.Message.RPCresponse;
import com.github.rholder.retry.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
public class GuavaRetry {

    private RpcClient rpcClient;

    public RPCresponse sendResponseWithRetry(RPCrequest request, RpcClient rpcClient){
        this.rpcClient=rpcClient;
        Retryer<RPCresponse> retryer = RetryerBuilder.<RPCresponse>newBuilder()
                .retryIfException(e->{
                    if (e instanceof SocketTimeoutException) return true;   // 超时，可能成功
                    if (e instanceof ConnectException) return true;         // 连接拒绝，可能成功
                    if (e instanceof NullPointerException) return false;    // Bug，不重试
                    if (e instanceof IllegalArgumentException) return false;// 参数错，不重试
                    return false;  // 默认不重试
                })
                .retryIfResult(response -> response == null|| Objects.equals(response.getCode(),500))
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(
                        new RetryListener() {
                            @Override
                            public <V> void onRetry(Attempt<V> attempt) {
                                log.info("第{}次重试", attempt.getAttemptNumber());
                            }
                        }

                ).build();
        try{
            return retryer.call(()-> rpcClient.sendRequest(request));
        }catch(Exception e){
            e.printStackTrace();
        }
        return RPCresponse.fail(500, "服务端返回失败");
    }
}
