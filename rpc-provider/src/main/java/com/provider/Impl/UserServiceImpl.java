package com.provider.Impl;

import com.Service.UserService;
import com.annotation.Retryable;
import com.pojo.User;
import java.util.UUID;
public class UserServiceImpl implements UserService {

    @Retryable
    @Override
    public User getUserByUserId(Integer userId) {
        System.out.println("查询userId为"+userId+"的用户信息");
        return User.builder().name(UUID.randomUUID().toString())
                .sex(true)
                .id(userId).build();

    }

    @Override
    public Integer insertUser(User user) {
        System.out.println("成功插入一位用户");
        return user.getId();
    }
}
