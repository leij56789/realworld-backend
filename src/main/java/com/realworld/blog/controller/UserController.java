package com.realworld.blog.controller;

import com.realworld.blog.annotation.Log;
import com.realworld.blog.dto.request.UserUpdateRequest;
import com.realworld.blog.dto.request.UsersLoginRequest;
import com.realworld.blog.dto.request.UsersRequest;
import com.realworld.blog.dto.response.UserAuthResponse;
import com.realworld.blog.dto.response.UserUpdateResponse;
import com.realworld.blog.dto.response.UsersLoginResponse;
import com.realworld.blog.dto.response.UsersResponse;
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
    public UsersLoginResponse usersLogin(@RequestBody UsersLoginRequest usersLoginRequest){
        return userService.usersLogin(usersLoginRequest);
    }
//    @PassToken
    @Log("用户注册")
    @PostMapping("/users")
    public UsersResponse users(@RequestBody UsersRequest usersRequest){
        return userService.users(usersRequest);
    }
    @Log("用户验证")
    @GetMapping("/user")
    public UserAuthResponse userAuth(){
        return userService.userAuth();
    }
    @Log("用户更新")
    @PutMapping("/user")
    public UserUpdateResponse userUpdate(@RequestBody UserUpdateRequest userUpdateRequest){
        return userService.userUpdate(userUpdateRequest);
    }
}