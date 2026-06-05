package com.rpc.Server.server.impl;

import com.rpc.Server.provider.ServiceProvider;
import com.rpc.Server.server.RpcServer;
import com.rpc.Server.server.work.WorkThread;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class ThreadPoolRPCRPCServer implements RpcServer {

    private ServiceProvider serviceProvider;

    private final ThreadPoolExecutor executor ;

    public  ThreadPoolRPCRPCServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
        executor=new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),  // ③ 核心线程数 = CPU 核数
                1000,                                        // ④ 最大线程数 = 1000
                60, TimeUnit.SECONDS,                        // ⑤ 空闲线程存活时间
                new ArrayBlockingQueue<>(100)                // ⑥ 任务队列容量 = 100
        );
    }

    public ThreadPoolRPCRPCServer(ServiceProvider serviceProvider,
                                  int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  BlockingQueue<Runnable> workQueue){
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, unit, workQueue);
        this.serviceProvider = serviceProvider;
    }
    @Override
    public void start(int port) {
        try{
            ServerSocket serverSocket=new ServerSocket(port);
            while(true){
                Socket socket=serverSocket.accept();
                executor.execute(new WorkThread(socket,serviceProvider));
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() {

    }
}
