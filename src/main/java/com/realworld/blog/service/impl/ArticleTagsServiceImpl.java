package com.realworld.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.realworld.blog.entity.ArticleTags;
import com.realworld.blog.service.ArticleTagsService;
import com.realworld.blog.mapper.ArticleTagsMapper;
import org.springframework.stereotype.Service;

/**
* @author jiaolei
* @description 针对表【article_tags】的数据库操作Service实现
* @createDate 2026-06-08 19:43:30
*/
@Service
public class ArticleTagsServiceImpl extends ServiceImpl<ArticleTagsMapper, ArticleTags>
    implements ArticleTagsService{

}




