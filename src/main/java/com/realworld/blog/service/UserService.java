package com.realworld.blog.service;

import com.realworld.blog.dto.request.UpdateUserRequest;
import com.realworld.blog.dto.request.LoginUserRequest;
import com.realworld.blog.dto.request.RegisterUser;
import com.realworld.blog.dto.response.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.realworld.blog.entity.User;

/**
* @author jiaolei
* @description 针对表【user】的数据库操作Service
* @createDate 2026-06-08 19:43:30
*/
public interface UserService extends IService<User> {

    LoginUserResponse loginUser(LoginUserRequest loginUserRequest);

    RegisterUserResponse registerUser(RegisterUser registerUser);

    GetUserResponse getUser();

    UpdateUserResponse updateUser(UpdateUserRequest updateUserRequest);

}
