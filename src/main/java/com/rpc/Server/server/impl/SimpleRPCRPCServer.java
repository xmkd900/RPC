package com.rpc.Server.server.impl;

import ch.qos.logback.classic.net.SimpleSocketServer;
import com.rpc.Server.provider.ServiceProvider;
import com.rpc.Server.server.RpcServer;
import com.rpc.Server.server.work.WorkThread;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@AllArgsConstructor
public class SimpleRPCRPCServer implements RpcServer {
    private ServiceProvider serviceProvider;

    @Override
    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true){
                Socket socket= serverSocket.accept();
                new Thread(new WorkThread(
                        socket,
                        serviceProvider

                )).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
