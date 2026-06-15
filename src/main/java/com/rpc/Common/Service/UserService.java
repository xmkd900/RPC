package com.rpc.Common.Service;

import com.rpc.Common.annotation.Retryable;
import com.rpc.Common.pojo.User;

public interface UserService {
    @Retryable
    public User getUserByUserId(Integer userId);
    public Integer insertUser(User user);


}
