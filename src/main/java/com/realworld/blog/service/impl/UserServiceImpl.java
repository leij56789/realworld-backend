package com.realworld.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.realworld.blog.common.BusinessException;
import com.realworld.blog.dto.request.UserUpdateRequest;
import com.realworld.blog.dto.request.UsersLoginRequest;
import com.realworld.blog.dto.request.UsersRequest;
import com.realworld.blog.dto.response.*;
import com.realworld.blog.entity.User;
import com.realworld.blog.interceptor.JwtInterceptor;
import com.realworld.blog.service.UserService;
import com.realworld.blog.mapper.UserMapper;
import com.realworld.blog.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
* @author jiaolei
* @description 针对表【user】的数据库操作Service实现
* @createDate 2026-06-08 19:43:30
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    UserMapper userMapper;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
//    @Override
//    public UsersLoginResponse usersLogin(UsersLoginRequest usersLoginRequest) {
//        String email = usersLoginRequest.getUser().getEmail();
//        String password = usersLoginRequest.getUser().getPassword();
////        String encodePassword = bCryptPasswordEncoder.encode(password);
//        if(StrUtil.isBlankIfStr(email)||StrUtil.isBlankIfStr(password)){
//            throw new BusinessException("email or password is empty");
//        }
//
//        User user = this.lambdaQuery().eq(User::getEmail,email).one();
//
//        if(user==null){
//            throw new BusinessException("email is invalid");
//        }
//        if(!bCryptPasswordEncoder.matches(password,user.getPassword())){
//            System.out.println("数据库中密码: " + user.getPassword());
//            System.out.println("前端密码长度: " + password.length());
//            throw new BusinessException("password is invalid");
//        }
//
//        String username = user.getUsername();
//        String token = jwtUtil.generateToken(username);
//
//        UsersLoginResponse usersLoginResponse = new UsersLoginResponse();
//        usersLoginResponse.setUser(new UsersLoginResponse.UserBean());
//        BeanUtils.copyProperties(user,usersLoginResponse.getUser());
//
//        usersLoginResponse.getUser().setToken(token);
//        return usersLoginResponse;
//    }

@Override
public UsersLoginResponse usersLogin(UsersLoginRequest usersLoginRequest) {
    String email = usersLoginRequest.getUser().getEmail();
    String password = usersLoginRequest.getUser().getPassword();

    System.out.println("=== 登录调试 ===");
    System.out.println("前端邮箱: " + email);
    System.out.println("前端密码: " + password);

    if (StrUtil.isBlank(email) || StrUtil.isBlank(password)) {
        throw new BusinessException("email or password is empty");
    }

    User user = this.lambdaQuery().eq(User::getEmail, email).one();
    if (user == null) {
        throw new BusinessException("email is invalid");
    }

    System.out.println("数据库密码: " + user.getPassword());
    System.out.println("密码匹配结果: " + bCryptPasswordEncoder.matches(password, user.getPassword()));

    if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
        throw new BusinessException("password is invalid");
    }

    // ... 后续代码
    String username = user.getUsername();
        String token = jwtUtil.generateToken(username);

        UsersLoginResponse usersLoginResponse = new UsersLoginResponse();
        usersLoginResponse.setUser(new UsersLoginResponse.UserBean());
        BeanUtils.copyProperties(user,usersLoginResponse.getUser());

        usersLoginResponse.getUser().setToken(token);
        return usersLoginResponse;
}

    @Override
    public UsersResponse users(UsersRequest usersRequest) {
        String username = usersRequest.getUser().getUsername();
        String email = usersRequest.getUser().getEmail();
        String password = usersRequest.getUser().getPassword();
        if(StrUtil.isBlankIfStr(username)||StrUtil.isBlankIfStr(email)||StrUtil.isBlankIfStr(password)){
            throw new BusinessException("username or email or password is empty");
        }

        if(this.lambdaQuery().eq(User::getUsername,username).exists()){
            throw new BusinessException("username already exists");
        }
        if(this.lambdaQuery().eq(User::getEmail,email).exists()){
            throw new BusinessException("email already exists");
        }
        String encodePassword = bCryptPasswordEncoder.encode(password);
        User user = new User();
        BeanUtils.copyProperties(usersRequest.getUser(),user);
        user.setPassword(encodePassword);
        int result = userMapper.insert(user);

        String token = jwtUtil.generateToken(usersRequest.getUser().getUsername());
        UsersResponse usersResponse = new UsersResponse();
        usersResponse.setUser(new UsersResponse.UserBean());
        BeanUtils.copyProperties(usersRequest.getUser(),usersResponse.getUser());
        usersResponse.getUser().setToken(token);
        return usersResponse;
    }


    @Override
    public UserAuthResponse userAuth() {
//        if(StrUtil.isBlankIfStr(authorization)){
//            throw new BusinessException("token is empty");
//        }
//        String token = authorization.substring(6);
//        String username = jwtUtil.getUsernameFromToken(token);

        String username = JwtInterceptor.getCurrentUser();
        String token = JwtInterceptor.getCurrentToken();
        User user = this.lambdaQuery().eq(User::getUsername, username).one();
        if(user==null){
            throw new BusinessException("username is invalid");
        }

        UserAuthResponse userAuthResponse = new UserAuthResponse();
        userAuthResponse.setUser(new UserAuthResponse.UserBean());
        BeanUtils.copyProperties(user, userAuthResponse.getUser());
        userAuthResponse.getUser().setToken(token);

        return userAuthResponse;
    }

    @Override
    public UserUpdateResponse userUpdate(UserUpdateRequest userUpdateRequest) {
        String bio = userUpdateRequest.getUser().getBio();
        String image = userUpdateRequest.getUser().getImage();
        String email = userUpdateRequest.getUser().getEmail();
        String username = JwtInterceptor.getCurrentUser();
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        if(StrUtil.isNotBlank(bio)){
            wrapper.set(User::getBio,bio);
        }
        if(StrUtil.isNotBlank(image)){
            wrapper.set(User::getImage,image);
        }
        if(StrUtil.isNotBlank(email)){
            if(this.lambdaQuery().eq(User::getEmail,email).ne(User::getUsername,username).exists()){
                throw new BusinessException("email already exist");
            }
            wrapper.set(User::getEmail,email);
        }
        wrapper.eq(User::getUsername,username);
        boolean result = this.update(wrapper);
//        this.lambdaUpdate()
//                .set(User::getBio,bio)
//                .set(User::getImage,image)
//                .set(User::getEmail,email)
//                .eq(User::getUsername,username).update();
        User user = this.lambdaQuery().eq(User::getUsername, username).one();

        UserUpdateResponse userUpdateResponse = new UserUpdateResponse();
        userUpdateResponse.setUser(new UserUpdateResponse.UserBean());
        BeanUtils.copyProperties(user,userUpdateResponse.getUser());
        userUpdateResponse.getUser().setToken(JwtInterceptor.getCurrentToken());
        return userUpdateResponse;
    }

}




