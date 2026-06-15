package com.realworld.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.realworld.blog.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jiaolei
* @description 针对表【user】的数据库操作Mapper
* @createDate 2026-06-08 19:43:30
* @Entity com.realworld.blog.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




