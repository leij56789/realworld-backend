package com.realworld.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.realworld.blog.dto.response.CommentsCreateResponse;
import com.realworld.blog.dto.response.CommentsListResponse;
import com.realworld.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author jiaolei
* @description 针对表【comment】的数据库操作Mapper
* @createDate 2026-06-08 19:43:30
* @Entity com.realworld.blog.entity.Comment
*/
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    CommentsCreateResponse.CommentBean getCommentBean(String currentUsername, String slug);

    Boolean insertCommentBySlugAndUsernameAndBody(String slug, String currentUsername, String body);

    CommentsCreateResponse.CommentBean getCommentBeanByCommentId(Long id);

    List<CommentsListResponse.CommentsBean> getCommentBeanBySlug(String slug);

    List<CommentsListResponse.CommentsBean> getCommentBeanByArticleId(Long id);
}




