package com.realworld.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.realworld.blog.dto.request.ListFeedArticlesRequest;
import com.realworld.blog.dto.request.ListArticlesRequest;
import com.realworld.blog.dto.request.CreateArticleRequest;
import com.realworld.blog.dto.request.UpdateArticleRequest;
import com.realworld.blog.dto.response.*;
import com.realworld.blog.entity.Article;

/**
* @author jiaolei
* @description 针对表【article】的数据库操作Service
* @createDate 2026-06-08 19:43:30
*/
public interface ArticleService extends IService<Article> {

    CreateArticleResponse createArticle(CreateArticleRequest articlesPostRequest);

    GetArticleResponse getArticle(String slug);

    ListArticlesResponse listArticles(ListArticlesRequest listArticlesRequest);

    UpdateArticleResponse updateArticle(UpdateArticleRequest updateArticleRequest, String slug);

    DeleteArticleResponse deleteArticle(String slug);

    FavoriteArticleResponse favoriteArticle(String slug);

    UnfavoriteArticleResponse unfavoriteArticle(String slug);

    ListFeedArticlesResponse listFeedArticles(ListFeedArticlesRequest listFeedArticlesRequest);
}
