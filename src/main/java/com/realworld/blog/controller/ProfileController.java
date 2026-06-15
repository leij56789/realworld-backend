package com.realworld.blog.controller;

import com.realworld.blog.dto.response.UnfollowUserResponse;
import com.realworld.blog.dto.response.FollowUserResponse;
import com.realworld.blog.dto.response.GetProfileResponse;
import com.realworld.blog.service.UserFollowsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author jiaolei
 * @date 2026-06-10 15:31
 * @description TODO
 */
@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    @Autowired
    UserFollowsService userFollowsService;
    @GetMapping("/{username}")
    public GetProfileResponse getProfile(@PathVariable String username){
        return userFollowsService.getProfile(username);
    }
    @PostMapping("/{username}/follow")
    public FollowUserResponse followUser(@PathVariable String username){
        return userFollowsService.followUser(username);
    }
    @DeleteMapping("/{username}/follow")
    public UnfollowUserResponse unfollowUser(@PathVariable String username){
        return userFollowsService.unfollowUser(username);
    }

}