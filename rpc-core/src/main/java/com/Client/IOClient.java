package com.Client;

import com.common.Message.RPCrequest;
import com.common.Message.RPCresponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class IOClient {
    public static RPCresponse sendRequest(String host, int port, RPCrequest rpcRequest) {
        try {
            Socket socket=new Socket(host, port);// 发起TCP请求
            //先建立输出流（防止死锁）
            ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
            oos.writeObject(rpcRequest); //序列化发送出去
            oos.flush();  //刷新缓冲区
           RPCresponse result=(RPCresponse) ois.readObject();  //阻塞等待服务端返回结果
           return result;

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;

        }

    }
}
