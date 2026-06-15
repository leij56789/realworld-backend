package com.realworld.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.realworld.blog.entity.UserFavorites;
import com.realworld.blog.service.UserFavoritesService;
import com.realworld.blog.mapper.UserFavoritesMapper;
import org.springframework.stereotype.Service;

/**
* @author jiaolei
* @description 针对表【user_favorites】的数据库操作Service实现
* @createDate 2026-06-08 19:43:30
*/
@Service
public class UserFavoritesServiceImpl extends ServiceImpl<UserFavoritesMapper, UserFavorites>
    implements UserFavoritesService{

}




