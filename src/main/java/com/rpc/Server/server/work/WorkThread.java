package com.rpc.Server.server.work;

import com.rpc.Common.Message.RPCrequest;
import com.rpc.Common.Message.RPCresponse;
import com.rpc.Server.provider.ServiceProvider;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

@AllArgsConstructor
@NoArgsConstructor
public class WorkThread implements Runnable {
    private Socket socket;
    private ServiceProvider serviceProvider;

    @Override
    public void run() {
        try {
            ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
            //读取客户端请求
           RPCrequest result=(RPCrequest) (ois.readObject());
            //处理请求
            RPCresponse response=getRPCresponse(result);
            //返回结果
            oos.writeObject(response);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private RPCresponse getRPCresponse(RPCrequest request) {
        //获取服务名
        String interfaceName=request.getInterfaceName();
        // 获取服务相应实现类
        Object service=serviceProvider.getService(interfaceName);
        //调用方法
        try{
            Method method=service.getClass().getMethod(request.getMethodName(),request.getParameterTypes());
            Object result=method.invoke(service,request.getParameters());
            return RPCresponse.success(result);

    }catch (Exception e){
            e.printStackTrace();
            return RPCresponse.fail(500,"方法错误");
        }

    }
}
