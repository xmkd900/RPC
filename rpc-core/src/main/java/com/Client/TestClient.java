package com.Client;


import com.Client.proxy.ClientProxy;
import com.Service.UserService;
import com.pojo.User;

/**
 * @author wxx
 * @version 1.0
 * @create 2024/2/6 18:39
 */
public class TestClient {
    public static void main(String[] args) {
        ClientProxy clientProxy=new ClientProxy();
        UserService proxy=clientProxy.getProxy(UserService.class);

        User user = proxy.getUserByUserId(1);
        System.out.println("从服务端得到的user="+user.toString());

        User u=User.builder().id(100).name("wxx").sex(true).build();
        Integer id = proxy.insertUser(u);
        System.out.println("向服务端插入user的id"+id);
    }
}
