package com.realworld.blog.controller;

import com.realworld.blog.annotation.Log;
import com.realworld.blog.dto.response.ListTagsResponse;
import com.realworld.blog.service.ArticleService;
import com.realworld.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiaolei
 * @date 2026-06-14 15:45
 * @description TODO
 */

@RestController
@RequestMapping("/api/tags")
public class TabController {
    @Autowired
    ArticleService articleService;
    @Autowired
    TagService tagService;
    //    @Auth
    @Log("获取标签列表")
    @GetMapping("")
    public ListTagsResponse listTags(){
        return tagService.tagList();
    }
}