package com.realworld.blog.controller;

import com.realworld.blog.dto.response.ProfilesUserFollowDeleteResponse;
import com.realworld.blog.dto.response.ProfilesUserFollowResponse;
import com.realworld.blog.dto.response.ProfilesUserResponse;
import com.realworld.blog.service.UserFollowsService;
import com.realworld.blog.service.UserService;
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
    public ProfilesUserResponse profilesUser(@PathVariable String username){
        return userFollowsService.profilesUser(username);
    }
    @PostMapping("/{username}/follow")
    public ProfilesUserFollowResponse profilesUserFollow(@PathVariable String username){
        return userFollowsService.profilsesUserFollow(username);
    }
    @DeleteMapping("/{username}/follow")
    public ProfilesUserFollowDeleteResponse profilesUserFollowDelete(@PathVariable String username){
        return userFollowsService.profilsesUserFollowDelete(username);
    }

}