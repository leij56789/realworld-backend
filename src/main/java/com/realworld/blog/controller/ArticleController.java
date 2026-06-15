package com.realworld.blog.controller;

import com.realworld.blog.annotation.Auth;
import com.realworld.blog.annotation.Log;
import com.realworld.blog.dto.request.ArticlesFeedRequest;
import com.realworld.blog.dto.request.ArticlesGetRequest;
import com.realworld.blog.dto.request.ArticlesPostRequest;
import com.realworld.blog.dto.request.ArticlesSlugPutRequest;
import com.realworld.blog.dto.response.*;
import com.realworld.blog.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author jiaolei
 * @date 2026-06-11 13:06
 * @description TODO
 */
@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    @Autowired
    ArticleService articleService;

    @Auth
    @Log("创建文章")
    @PostMapping("")
    public ArticlesPostResponse articlesPost(@RequestBody ArticlesPostRequest articlesPostRequest){
        return articleService.articlesPost(articlesPostRequest);
    }
//    @Auth
    @Log("获取单片文章")
    @GetMapping("/{slug}")
    public ArticlesSlugGetResponse articlesSlugGet(@PathVariable String slug){
        return articleService.articlesSlugGet(slug);
    }
//    @Auth
    @Log("更新文章")
    @PutMapping("/{slug}")
    public ArticlesSlugPutResponse articlesSlugPut(@RequestBody ArticlesSlugPutRequest articlesSlugPutRequest
    , @PathVariable String slug){
        return articleService.articlesSlugPut(articlesSlugPutRequest,slug);
    }
//    @Auth
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Log("删除文章")
    @DeleteMapping("/{slug}")
    public ArticlesSlugDeleteResponse articlesSlugDelete(@PathVariable String slug){
        return articleService.articlesSlugDelete(slug);
    }
//    @Auth
    @Log("获取文章列表")
    @GetMapping("")
    public ArticlesGetResponse articlesGet(ArticlesGetRequest articlesGetRequest){
        return articleService.articlesGet(articlesGetRequest);
    }
//    @Auth
    @Log("获取Feed文章")
    @GetMapping("/feed")
    public ArticlesFeedResponse articlesFeed(ArticlesFeedRequest articlesFeedRequest){
        return articleService.articlesFeed(articlesFeedRequest);
    }
//    @Auth
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Log("点赞文章")
    @PostMapping("/{slug}/favorite")
    public ArticlesSlugFavoritePostResponse articlesSlugFavoritePost(@PathVariable String slug){
        return articleService.articlesSlugFavoritePost(slug);
    }
//    @Auth
    @Log("取消点赞")
    @DeleteMapping("/{slug}/favorite")
    public ArticlesSlugFavoriteDeleteResponse articlesSlugFavoriteDelete(@PathVariable String slug){
        return articleService.articlesSlugFavoriteDelete(slug);
    }

}