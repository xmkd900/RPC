package com.Service;
import com.annotation.Retryable;
import com.pojo.User;

public interface UserService {
    @Retryable
    public User getUserByUserId(Integer userId);
    public Integer insertUser(User user);


}
