package com.realworld.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.realworld.blog.dto.request.CommentsCreateRequest;
import com.realworld.blog.dto.response.CommentDeleteResponse;
import com.realworld.blog.dto.response.CommentsCreateResponse;
import com.realworld.blog.dto.response.CommentsListResponse;
import com.realworld.blog.entity.Comment;

/**
* @author jiaolei
* @description 针对表【comment】的数据库操作Service
* @createDate 2026-06-08 19:43:30
*/
public interface CommentService extends IService<Comment> {

    CommentsListResponse commentsList(String slug);

    CommentsCreateResponse commentsCreate(CommentsCreateRequest commentsCreateRequest, String slug);

    CommentDeleteResponse commentDelete(String slug, Long id);
}
