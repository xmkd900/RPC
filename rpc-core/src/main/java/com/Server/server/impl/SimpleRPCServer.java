package com.Server.server.impl;


import com.Server.provider.ServiceProvider;
import com.Server.server.RpcServer;
import com.Server.server.work.WorkThread;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@AllArgsConstructor
public class SimpleRPCServer implements RpcServer {
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
