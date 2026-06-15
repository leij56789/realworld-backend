package com.realworld.blog.controller;

import com.realworld.blog.annotation.Log;
import com.realworld.blog.dto.request.UpdateUserRequest;
import com.realworld.blog.dto.request.LoginUserRequest;
import com.realworld.blog.dto.request.RegisterUser;
import com.realworld.blog.dto.response.GetUserResponse;
import com.realworld.blog.dto.response.UpdateUserResponse;
import com.realworld.blog.dto.response.LoginUserResponse;
import com.realworld.blog.dto.response.RegisterUserResponse;
import com.realworld.blog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author jiaolei
 * @date 2026-06-08 18:40
 * @description TODO
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

//    @PassToken
    @Log("用户登录")
    @PostMapping("/users/login")
    public LoginUserResponse loginUser(@RequestBody LoginUserRequest loginUserRequest){
        return userService.loginUser(loginUserRequest);
    }
//    @PassToken
    @Log("用户注册")
    @PostMapping("/users")
    public RegisterUserResponse registerUser(@RequestBody RegisterUser registerUser){
        return userService.registerUser(registerUser);
    }
    @Log("用户验证")
    @GetMapping("/user")
    public GetUserResponse getUser(){
        return userService.getUser();
    }
    @Log("用户更新")
    @PutMapping("/user")
    public UpdateUserResponse updateUser(@RequestBody UpdateUserRequest updateUserRequest){
        return userService.updateUser(updateUserRequest);
    }
}