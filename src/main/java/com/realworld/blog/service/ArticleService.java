package com.realworld.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.realworld.blog.dto.request.ArticlesFeedRequest;
import com.realworld.blog.dto.request.ArticlesGetRequest;
import com.realworld.blog.dto.request.ArticlesPostRequest;
import com.realworld.blog.dto.request.ArticlesSlugPutRequest;
import com.realworld.blog.dto.response.*;
import com.realworld.blog.entity.Article;

/**
* @author jiaolei
* @description 针对表【article】的数据库操作Service
* @createDate 2026-06-08 19:43:30
*/
public interface ArticleService extends IService<Article> {

    ArticlesPostResponse articlesPost(ArticlesPostRequest articlesPostRequest);

    ArticlesSlugGetResponse articlesSlugGet(String slug);

    ArticlesGetResponse articlesGet(ArticlesGetRequest articlesGetRequest);

    ArticlesSlugPutResponse articlesSlugPut(ArticlesSlugPutRequest articlesSlugPutRequest, String slug);

    ArticlesSlugDeleteResponse articlesSlugDelete(String slug);

    ArticlesSlugFavoritePostResponse articlesSlugFavoritePost(String slug);

    ArticlesSlugFavoriteDeleteResponse articlesSlugFavoriteDelete(String slug);

    ArticlesFeedResponse articlesFeed(ArticlesFeedRequest articlesFeedRequest);
}
