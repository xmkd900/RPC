package com.rpc.Common.Service;

import com.rpc.Common.pojo.User;

public interface UserService {
    public User getUserByUserId(Integer userId);
    public Integer insertUser(User user);


}
