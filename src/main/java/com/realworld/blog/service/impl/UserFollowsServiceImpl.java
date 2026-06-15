package com.realworld.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.realworld.blog.common.BusinessException;
import com.realworld.blog.dto.response.ProfilesUserFollowDeleteResponse;
import com.realworld.blog.dto.response.ProfilesUserFollowResponse;
import com.realworld.blog.dto.response.ProfilesUserResponse;
import com.realworld.blog.entity.User;
import com.realworld.blog.entity.UserFollows;
import com.realworld.blog.interceptor.JwtInterceptor;
import com.realworld.blog.mapper.UserMapper;
import com.realworld.blog.service.UserFollowsService;
import com.realworld.blog.mapper.UserFollowsMapper;
import com.realworld.blog.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.Literal;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
* @author jiaolei
* @description 针对表【user_follows】的数据库操作Service实现
* @createDate 2026-06-08 19:43:30
*/
@Service
public class UserFollowsServiceImpl extends ServiceImpl<UserFollowsMapper, UserFollows>
    implements UserFollowsService{
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserService userServiceImpl;

    @Override
    public ProfilesUserResponse profilesUser(String username) {
        String currentUsername= JwtInterceptor.getCurrentUser();
        if(StrUtil.isBlank(username)){
            throw new BusinessException("username is empty");
        }
        ProfilesUserResponse profilesUserResponse = new ProfilesUserResponse();
        profilesUserResponse.setProfile(new ProfilesUserResponse.ProfileBean());

        User user = userServiceImpl.lambdaQuery().eq(User::getUsername, username).one();
        if(user==null){
            throw new BusinessException("user not found");
        }
        if(StrUtil.isNotBlank(currentUsername)){
            User currentUser = userServiceImpl.lambdaQuery().eq(User::getUsername, currentUsername).one();
            if(this.lambdaQuery().eq(UserFollows::getFollowerId,currentUser.getId())
                    .eq(UserFollows::getFolloweeId,user.getId()).exists()){
                profilesUserResponse.getProfile().setFollowing(true);
            }
        }

        BeanUtils.copyProperties(user,profilesUserResponse.getProfile());
        return profilesUserResponse;
    }

    @Override
    public ProfilesUserFollowResponse profilsesUserFollow(String username) {
        String currentUsername = JwtInterceptor.getCurrentUser();
        if(StrUtil.isBlank(currentUsername)){
            throw new BusinessException("user not logged in");
        }
        if(StrUtil.isBlank(username)){
            throw new BusinessException("username is empty");
        }
        if(currentUsername.equals(username)){
            throw new BusinessException("cannot follow yourself");
        }

        List<User> list = userServiceImpl.lambdaQuery().in(User::getUsername, Arrays.asList(username, currentUsername)).list();
        if(list.size()<2){
            throw new BusinessException("user not found");
        }
        User currentUser = list.stream()
                .filter(u -> u.getUsername().equals(currentUsername))
                .findFirst()
                .orElseThrow(() -> new BusinessException("current user not found"));
        User user = list.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new BusinessException("user not found"));
        if((this.lambdaQuery().eq(UserFollows::getFollowerId,currentUser.getId())
            .eq(UserFollows::getFolloweeId,user.getId())).exists()){
            throw new BusinessException("already followed");
        }
        UserFollows userFollows = new UserFollows();
        userFollows.setFollowerId(currentUser.getId());
        userFollows.setFolloweeId(user.getId());
        this.save(userFollows);

        ProfilesUserFollowResponse profilesUserFollowResponse = new ProfilesUserFollowResponse();
        profilesUserFollowResponse.setProfile(new ProfilesUserFollowResponse.ProfileBean());
        BeanUtils.copyProperties(user,profilesUserFollowResponse.getProfile());
        profilesUserFollowResponse.getProfile().setFollowing(true);
        return profilesUserFollowResponse;
    }

    @Override
    public ProfilesUserFollowDeleteResponse profilsesUserFollowDelete(String username) {
        //用两个name查询两个user,两个id查询follows再删除
        if(username==null){
            throw new BusinessException("username is empty");
        }
        String currentUsername = JwtInterceptor.getCurrentUser();
        if(currentUsername==null){
            throw new BusinessException("user not logged in");
        }
        if(currentUsername.equals(username)){
            throw new BusinessException("cannot unfollow yourself");
        }

        List<User> list = userServiceImpl.lambdaQuery().in(User::getUsername, Arrays.asList(username, currentUsername)).list();
        if(list.size()<2){
            throw new BusinessException("user not found");
        }
        User targetUser = list.stream().filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new BusinessException("target user not found"));
        User currentUser = list.stream().filter(u -> u.getUsername().equals(currentUsername))
                .findFirst()
                .orElseThrow(() -> new BusinessException("current user not found"));
        if(currentUser==null){
            throw new BusinessException("current user not found");
        }
        LambdaQueryWrapper<UserFollows> wrapper = new LambdaQueryWrapper<UserFollows>().eq(UserFollows::getFollowerId, currentUser.getId())
                .eq(UserFollows::getFolloweeId, targetUser.getId());
        if(this.getOne(wrapper)==null){
            throw new BusinessException("not followed yet");
        }
        this.remove(wrapper);

        ProfilesUserFollowDeleteResponse profilesUserFollowDeleteResponse = new ProfilesUserFollowDeleteResponse();
        profilesUserFollowDeleteResponse.setProfile(new ProfilesUserFollowDeleteResponse.ProfileBean());
        BeanUtils.copyProperties(targetUser,profilesUserFollowDeleteResponse.getProfile());
        return profilesUserFollowDeleteResponse;
    }
}




