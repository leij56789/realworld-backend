package com.realworld.blog.controller;

import com.realworld.blog.annotation.Log;
import com.realworld.blog.dto.request.ArticlesPostRequest;
import com.realworld.blog.dto.request.CommentsCreateRequest;
import com.realworld.blog.dto.response.ArticlesPostResponse;
import com.realworld.blog.dto.response.CommentDeleteResponse;
import com.realworld.blog.dto.response.CommentsCreateResponse;
import com.realworld.blog.dto.response.CommentsListResponse;
import com.realworld.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author jiaolei
 * @date 2026-06-14 16:31
 * @description TODO
 */
@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    CommentService commentService;

    @Log("获取文章评论列表")
    @GetMapping("/articles/{slug}/comments")
    public CommentsListResponse commentsList(@PathVariable String slug){
        return commentService.commentsList(slug);
    }
    @Log("添加评论")
    @PostMapping("/articles/{slug}/comments")
    public CommentsCreateResponse commentsCreate(@RequestBody CommentsCreateRequest commentsCreateRequest,@PathVariable  String slug){
        return commentService.commentsCreate(commentsCreateRequest,slug);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Log("删除评论")
    @DeleteMapping("/articles/{slug}/comments/{id}")
    public CommentDeleteResponse commentDelete(@PathVariable String slug,@PathVariable Long id){
        return commentService.commentDelete(slug,id);
    }
}