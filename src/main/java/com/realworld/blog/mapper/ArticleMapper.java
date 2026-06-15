package com.realworld.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.realworld.blog.dto.response.ListFeedArticlesResponse;
import com.realworld.blog.dto.response.ListArticlesResponse;
import com.realworld.blog.dto.response.UpdateArticleResponse;
import com.realworld.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author jiaolei
* @description 针对表【article】的数据库操作Mapper
* @createDate 2026-06-08 19:43:30
* @Entity com.realworld.blog.entity.Article
*/
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    List<ListArticlesResponse.ArticlesDTO> getArticlesDTOList(String favorited, String tag, String author, int limit, int offset);

    Integer getArticlesDTOCount(String favorited, String tag, String author);

    List<ListArticlesResponse.ArticlesDTO> getArticlesDTOAllList(int limit, int offset);

    UpdateArticleResponse.ArticleBean getArticleBean(String slug);

    List<ListFeedArticlesResponse.ArticleBean> getArticlesDTOListByuserId(Long id, int limit, int offset);

    List<ListFeedArticlesResponse.ArticleBean> getArticlesDTOAllListByuserId(Long id);
}




