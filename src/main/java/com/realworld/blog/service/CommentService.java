package com.realworld.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.realworld.blog.dto.request.CreateCommentRequest;
import com.realworld.blog.dto.response.CreateCommentResponse;
import com.realworld.blog.dto.response.DeleteCommentResponse;
import com.realworld.blog.dto.response.ListCommentsResponse;
import com.realworld.blog.entity.Comment;

/**
* @author jiaolei
* @description 针对表【comment】的数据库操作Service
* @createDate 2026-06-08 19:43:30
*/
public interface CommentService extends IService<Comment> {

    ListCommentsResponse listComments(String slug);

    CreateCommentResponse createComment(CreateCommentRequest createCommentRequest, String slug);

    DeleteCommentResponse deleteComment(String slug, Long id);
}
