package com.realworld.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.realworld.blog.common.BusinessException;
import com.realworld.blog.dto.request.ArticlesFeedRequest;
import com.realworld.blog.dto.request.ArticlesGetRequest;
import com.realworld.blog.dto.request.ArticlesPostRequest;
import com.realworld.blog.dto.request.ArticlesSlugPutRequest;
import com.realworld.blog.dto.response.*;
import com.realworld.blog.entity.*;
import com.realworld.blog.interceptor.JwtInterceptor;
import com.realworld.blog.mapper.*;
import com.realworld.blog.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* @author jiaolei
* @description 针对表【article】的数据库操作Service实现
* @createDate 2026-06-08 19:43:30
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService {
    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    ArticleTagsMapper articleTagsMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserFavoritesMapper userFavoritesMapper;
    @Autowired
    ArticleTagsService articleTagsService;
    @Autowired
    UserService userService;
    @Autowired
    UserFavoritesService userFavoritesService;
    @Autowired
    TagService tagService;
    @Autowired
    UserFollowsService userFollowsService;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private UserFollowsMapper userFollowsMapper;

    @Transactional
    @Override
    public ArticlesPostResponse articlesPost(ArticlesPostRequest articlesPostRequest) {
        //req:select user insert article tag  select tag insert article_tag +slug
        //res:select user article favorite(默认不用写) +tag +following
        ArticlesPostRequest.ArticleBean articleBean = articlesPostRequest.getArticle();
        String currentUsername = JwtInterceptor.getCurrentUser();
        if (currentUsername == null) {
            throw new BusinessException("user not logged in");
        }

        if (articleBean == null) {
            throw new BusinessException("articleBean is empty");
        }
        Article article = new Article();
        BeanUtils.copyProperties(articleBean, article);
        article.setSlug(generateSlug(article.getTitle()));

        List<String> tagList = articleBean.getTagList();

        User currentUser = userService.lambdaQuery().eq(User::getUsername, currentUsername).one();
        if (currentUser == null) {
            throw new BusinessException("user not found");
        }
        article.setAuthorId(currentUser.getId());
//        if(this.lambdaQuery().eq(Article::getSlug,article.getSlug()).exists()){
//            throw new BusinessException("slug has existed");
//        }
        this.save(article);

        if (tagList != null && !tagList.isEmpty()) {
            List<ArticleTags> list = tagList.stream()
                    .filter(StrUtil::isNotBlank)
                    .map(tag -> {
                        ArticleTags articleTags = new ArticleTags();
                        Tag tagEntity = tagService.lambdaQuery().eq(Tag::getName, tag).one();

                        if (tagEntity == null) {
                            tagEntity = new Tag();
                            tagEntity.setName(tag);
                            tagService.save(tagEntity);
                        }
                        articleTags.setArticleId(article.getId());
                        articleTags.setTagId(tagEntity.getId());
                        return articleTags;
                    }).toList();
            if (list != null && !list.isEmpty()) {
                articleTagsService.saveBatch(list);
            }
        }
        Article articleRes = this.getById(article.getId());

        ArticlesPostResponse articlesPostResponse = new ArticlesPostResponse();
        articlesPostResponse.setArticle(new ArticlesPostResponse.ArticleBean());
        articlesPostResponse.getArticle().setAuthor(new ArticlesPostResponse.ArticleBean.AuthorBean());
        BeanUtils.copyProperties(articleRes, articlesPostResponse.getArticle());
        BeanUtils.copyProperties(currentUser, articlesPostResponse.getArticle().getAuthor());
        articlesPostResponse.getArticle().setTagList(articlesPostRequest.getArticle().getTagList());
        articlesPostResponse.getArticle().setSlug(articleRes.getSlug());
        return articlesPostResponse;
    }

    @Override
    public ArticlesSlugGetResponse articlesSlugGet(String slug) {
        //select article(slug) select user(author_id,username) select userfollows(2id)
        //select articletag(articleid) select tag(tagid),select userfav(cuserid,articleid)
        //article,tag,cu,fav,user,follow
        String currentUsername = JwtInterceptor.getCurrentUser();

        ArticlesSlugGetResponse articlesSlugGetResponse = new ArticlesSlugGetResponse();
        articlesSlugGetResponse.setArticle(new ArticlesSlugGetResponse.ArticleBean());
        articlesSlugGetResponse.getArticle().setAuthor(new ArticlesSlugGetResponse.ArticleBean.AuthorBean());

        Article article = this.lambdaQuery().eq(Article::getSlug, slug).one();
        if (article == null) {
            throw new BusinessException("article not found");
        }
        User targetUser = userService.getById(article.getAuthorId());
        if (targetUser == null) {
            throw new BusinessException("target user not found");
        }
        List<ArticleTags> list = articleTagsService.lambdaQuery().eq(ArticleTags::getArticleId, article.getId()).list();
        if (list != null && !list.isEmpty()) {
            List<String> tagNameList = list.stream()
                    .filter(articleTags -> articleTags.getTagId() != null)
                    .map(articleTags -> {
                        Long tagId = articleTags.getTagId();
                        Tag tag = tagService.getById(tagId);
                        if (tag == null) {
                            throw new BusinessException("tag not found");
                        }
                        return tag.getName();
                    }).toList();
            articlesSlugGetResponse.getArticle().setTagList(tagNameList);
        }
        List<UserFavorites> userFavoritesList = userFavoritesService.lambdaQuery().eq(UserFavorites::getArticleId, article.getId()).list();
        if(userFavoritesList!=null){
            articlesSlugGetResponse.getArticle().setFavoritesCount(userFavoritesList.size());
        }
        if (currentUsername != null) {
            User currentUser = userService.lambdaQuery().eq(User::getUsername, currentUsername).one();
            if (currentUser != null) {
                if (userFollowsService.lambdaQuery().eq(UserFollows::getFollowerId, currentUser.getId())
                        .eq(UserFollows::getFolloweeId, targetUser.getId()).exists()) {
                    articlesSlugGetResponse.getArticle().getAuthor().setFollowing(true);
                }
                if (userFavoritesService.lambdaQuery().eq(UserFavorites::getArticleId, article.getId())
                        .eq(UserFavorites::getUserId, currentUser.getId()).exists()) {
                    articlesSlugGetResponse.getArticle().setFavorited(true);
                }
            }
        }

        BeanUtils.copyProperties(article, articlesSlugGetResponse.getArticle());
        BeanUtils.copyProperties(targetUser, articlesSlugGetResponse.getArticle().getAuthor());
        articlesSlugGetResponse.getArticle().setCreatedAt(article.getCreatedAt());
        articlesSlugGetResponse.getArticle().setUpdatedAt(article.getUpdatedAt());
        return articlesSlugGetResponse;
    }

    @Override
    public ArticlesGetResponse articlesGet(ArticlesGetRequest articlesGetRequest) {
        System.out.println("limit进来");
        if(articlesGetRequest==null){
            throw new BusinessException("request is empty");
        }
        String favorited = articlesGetRequest.getFavorited();
        String tag = articlesGetRequest.getTag();
        String author = articlesGetRequest.getAuthor();
        int limit = articlesGetRequest.getLimit();
        int offset = articlesGetRequest.getOffset();
        if(limit<=0){
            limit=20;
        }
        if(offset<=0){
            offset=0;
        }

        String currentUsername = JwtInterceptor.getCurrentUser();

        ArticlesGetResponse articlesGetResponse = new ArticlesGetResponse();


        List<ArticlesGetResponse.ArticlesDTO> list=null;
        Integer articlesDTOCount=0;
        System.out.println("入口");
        if(favorited==null&&tag==null&&author==null){
            list= articleMapper.getArticlesDTOAllList(limit,offset);
            articlesDTOCount= Math.toIntExact(this.lambdaQuery().count());
        }else{
            System.out.println("tag查询");
            list=articleMapper.getArticlesDTOList(favorited,tag,author,limit,offset);
            articlesDTOCount=articleMapper.getArticlesDTOCount(favorited,tag,author);
        }
        if(list!=null&&!list.isEmpty()) {
            List<ArticlesGetResponse.ArticlesDTO> articlesDTOList = list.stream().map(articlesDTO -> {
                Long articlesDTOId = articlesDTO.getId();
                articlesDTO.setTagList(new ArrayList<>());
                List<String> tagnameList= tagMapper.selectTagnameByarticleId(articlesDTOId);
                System.out.println("tagnameList"+tagnameList.toString());
                if(tagnameList!=null&&!tagnameList.isEmpty()){
                    articlesDTO.setTagList(tagnameList);
                }
                Integer favoriteCount = Math.toIntExact(userFavoritesService.lambdaQuery().eq(UserFavorites::getArticleId, articlesDTOId).count());
                articlesDTO.setFavoritesCount(favoriteCount);
                articlesDTO.setAuthor(new ArticlesGetResponse.ArticlesDTO.AuthorDTO());
                User authorEntity = userService.getById(articlesDTO.getAuthorId());
                BeanUtils.copyProperties(authorEntity,articlesDTO.getAuthor());
                if(currentUsername!=null){
                    Integer favorite =userFavoritesMapper.selectFavoritedBycurrentUsernameAndarticleId(articlesDTOId,currentUsername);
                    if(favorite>0){
                        articlesDTO.setFavorited(true);
                    }
                    Integer following=userFollowsMapper.selectfollowingCountBycurrentUsernameAndAuthorId(currentUsername,articlesDTO.getAuthorId());
                    if(following>0){
                        articlesDTO.getAuthor().setFollowing(true);
                    }
                }

                articlesDTO.setAuthorId(null);
                articlesDTO.setId(null);
                return articlesDTO;
            }).toList();
            if(articlesDTOList!=null){
                articlesGetResponse.setArticles(articlesDTOList);
            }
        }
        articlesGetResponse.setArticlesCount(articlesDTOCount);
        return articlesGetResponse;
    }

    @Transactional
    @Override
    public ArticlesSlugPutResponse articlesSlugPut(ArticlesSlugPutRequest articlesSlugPutRequest, String slug) {
        //update article(title description body where slug)
        //select a.slug,a.title,a.description,a.body,a.createdAt,a.updatedAt,u.username,u.bio,u.image
        // from article a join user u on u.id=a.author_id where a.slug=#{slug}
        //res:res1+tagList+fav+favcount+following
        String currentUsername = JwtInterceptor.getCurrentUser();
        String body = articlesSlugPutRequest.getArticle().getBody();
        String title = articlesSlugPutRequest.getArticle().getTitle();
        String description = articlesSlugPutRequest.getArticle().getDescription();

        ArticlesSlugPutResponse articlesSlugPutResponse = new ArticlesSlugPutResponse();

        if(currentUsername==null){
            throw new BusinessException("user not logged in");
        }
        if(slug==null){
            throw new BusinessException("slug is empty");
        }
        Article article = this.lambdaQuery().eq(Article::getSlug, slug).one();
        if(article==null){
            throw new BusinessException("slug is invalid");
        }
        if(!userService.lambdaQuery().eq(User::getUsername,currentUsername).eq(User::getId,article.getAuthorId()).exists()){
            throw new BusinessException("permission denied");
        }
        LambdaUpdateWrapper<Article> articleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        if(body!=null){
            articleLambdaUpdateWrapper.set(Article::getBody,body);
        }
        String newSlug=null;
        if(title!=null){
            articleLambdaUpdateWrapper.set(Article::getTitle,title);
            newSlug = generateSlug(title);
            articleLambdaUpdateWrapper.set(Article::getSlug,newSlug);
        }
        if(description!=null){
            articleLambdaUpdateWrapper.set(Article::getDescription,description);
        }


        articleLambdaUpdateWrapper.eq(Article::getSlug,slug);


        int updated = articleMapper.update(articleLambdaUpdateWrapper);
        if(updated!=1){
            throw new BusinessException("update failed");
        }
        //select a.slug,a.title,a.description,a.body,a.createdAt,a.updatedAt,u.username,u.bio,u.image
        // from article a join user u on u.id=a.author_id where a.slug=#{slug}
        //res:res1+tagList+fav+favcount+following
        if(newSlug!=null){
            slug=newSlug;
        }
        ArticlesSlugPutResponse.ArticleBean articleBean=articleMapper.getArticleBean(slug);
        articleBean.setTagList(new ArrayList<>());
        Long articleId = articleBean.getId();
        List<String> tagnameList= tagMapper.selectTagnameByarticleId(articleId);
        System.out.println("tagnameList"+tagnameList.toString());
        if(tagnameList!=null&&!tagnameList.isEmpty()){
            articleBean.setTagList(tagnameList);
        }
        Integer favoriteCount = Math.toIntExact(userFavoritesService.lambdaQuery().eq(UserFavorites::getArticleId, articleId).count());
        articleBean.setFavoritesCount(favoriteCount);
        articleBean.setAuthor(new ArticlesSlugPutResponse.ArticleBean.AuthorBean());
        User authorEntity = userService.getById(articleBean.getAuthorId());
        BeanUtils.copyProperties(authorEntity,articleBean.getAuthor());
        if(currentUsername!=null){
            Integer favorite =userFavoritesMapper.selectFavoritedBycurrentUsernameAndarticleId(articleId,currentUsername);
            if(favorite>0){
                articleBean.setFavorited(true);
            }
            Integer following=userFollowsMapper.selectfollowingCountBycurrentUsernameAndAuthorId(currentUsername,articleBean.getAuthorId());
            if(following>0){
                articleBean.getAuthor().setFollowing(true);
            }
        }

        articleBean.setAuthorId(null);
        articleBean.setId(null);

        articlesSlugPutResponse.setArticle(articleBean);
        return articlesSlugPutResponse;
    }

    @Transactional
    @Override
    public ArticlesSlugDeleteResponse articlesSlugDelete(String slug) {
        String currentUsername = JwtInterceptor.getCurrentUser();
        if(currentUsername==null){
            throw new BusinessException("user not logged in");
        }
        if(slug==null){
            throw new BusinessException("slug is empty");
        }
        Article article = this.lambdaQuery().eq(Article::getSlug, slug).one();
        if(article==null){
            throw new BusinessException("article not found");
        }
        if(!userService.lambdaQuery().eq(User::getUsername,currentUsername)
                .eq(User::getId,article.getAuthorId()).exists()){
            throw new BusinessException("permission denied");
        }
        articleTagsService.lambdaUpdate().eq(ArticleTags::getArticleId,article.getId()).remove();
        userFavoritesService.lambdaUpdate().eq(UserFavorites::getArticleId,article.getId()).remove();
        boolean removed = this.lambdaUpdate().eq(Article::getSlug, slug).remove();
        if(!removed){
            throw new BusinessException("delete failed");
        }
        return null;
    }

    @Override
    public ArticlesSlugFavoritePostResponse articlesSlugFavoritePost(String slug) {
        //select article(slug) select user(currentUsername) insert fav(userId,articleId)
        String currentUsername = JwtInterceptor.getCurrentUser();

        if(currentUsername==null){
            throw new BusinessException("user not logged in");
        }
        if(slug==null){
            throw new BusinessException("slug is empty");
        }
        Article article = this.lambdaQuery().eq(Article::getSlug, slug).one();
        if(article==null){
            throw new BusinessException("article not found");
        }

        User user = userService.lambdaQuery().eq(User::getUsername, currentUsername).one();
        if(user==null){
            throw new BusinessException("user not found");
        }
        if(userFavoritesService.lambdaQuery().eq(UserFavorites::getUserId,user.getId())
                .eq(UserFavorites::getArticleId,article.getId()).exists()){
            throw new BusinessException("already liked");
        }
        UserFavorites userFavorites = new UserFavorites();
        userFavorites.setArticleId(article.getId());
        userFavorites.setUserId(user.getId());
        boolean saved = userFavoritesService.save(userFavorites);
        if(!saved){
            throw new BusinessException("insert failed");
        }
        return null;
    }

    @Transactional
    @Override
    public ArticlesSlugFavoriteDeleteResponse articlesSlugFavoriteDelete(String slug) {
        //select user(currentUsername) select article(slug) select userFav(userId,articleId)
        //delete userFav(serId,articleId)
        //select *,u.username,u.bio,u.image from article a join user u on a.authorId=u.id
        //where u.username=#{currentUsername} and a.slug=#{slug}
        String currentUsername = JwtInterceptor.getCurrentUser();

        if(currentUsername==null){
            throw new BusinessException("user not logged in");
        }
        if(slug==null){
            throw new BusinessException("slug is empty");
        }

        ArticlesSlugFavoriteDeleteResponse articlesSlugFavoriteDeleteResponse = new ArticlesSlugFavoriteDeleteResponse();
        articlesSlugFavoriteDeleteResponse.setArticle(new ArticlesSlugFavoriteDeleteResponse.ArticleBean());


        Boolean selected=userFavoritesMapper.selectFavoritedBycurrentUsernameAndSlug(currentUsername,slug);
        if(!selected){
            throw new BusinessException("not favorited yet");
        }
        Boolean deleted=userFavoritesMapper.deleteByUsernameAndSlug(currentUsername,slug);
        if(!deleted){
            throw new BusinessException("deleted failed");
        }
        ArticlesSlugPutResponse.ArticleBean articleBean = articleMapper.getArticleBean(slug);
        if(articleBean==null){
            throw new BusinessException("article not found");
        }
        Long articleId = articleBean.getId();
        List<String> tagnameList= tagMapper.selectTagnameByarticleId(articleId);
        System.out.println("tagnameList"+tagnameList.toString());
        if(tagnameList!=null&&!tagnameList.isEmpty()){
            articleBean.setTagList(tagnameList);
        }
        Integer favoriteCount = Math.toIntExact(userFavoritesService.lambdaQuery().eq(UserFavorites::getArticleId, articleId).count());
        articleBean.setFavoritesCount(favoriteCount);
        articleBean.setAuthor(new ArticlesSlugPutResponse.ArticleBean.AuthorBean());
        User authorEntity = userService.getById(articleBean.getAuthorId());
        BeanUtils.copyProperties(authorEntity,articleBean.getAuthor());
        if(currentUsername!=null){
            Integer favorite =userFavoritesMapper.selectFavoritedBycurrentUsernameAndarticleId(articleId,currentUsername);
            if(favorite>0){
                articleBean.setFavorited(true);
            }
            Integer following=userFollowsMapper.selectfollowingCountBycurrentUsernameAndAuthorId(currentUsername,articleBean.getAuthorId());
            if(following>0){
                articleBean.getAuthor().setFollowing(true);
            }
        }

        articleBean.setAuthorId(null);
        articleBean.setId(null);


        BeanUtils.copyProperties(articleBean,articlesSlugFavoriteDeleteResponse.getArticle());
        return articlesSlugFavoriteDeleteResponse;
    }

    @Override
    public ArticlesFeedResponse articlesFeed(ArticlesFeedRequest articlesFeedRequest) {
        ArticlesFeedResponse articlesFeedResponse = new ArticlesFeedResponse();
        articlesFeedResponse.setArticles(new ArrayList<ArticlesFeedResponse.ArticleBean>());
        int limit = articlesFeedRequest.getLimit();
        int offset = articlesFeedRequest.getOffset();
        if(limit<=0){
            limit=20;
        }
        if(offset<=0){
            offset=0;
        }
        String currentUsername = JwtInterceptor.getCurrentUser();
        if(StrUtil.isBlank(currentUsername)){
           throw new BusinessException("user not logged in");
        }
        User user = userService.lambdaQuery().eq(User::getUsername, currentUsername).one();
        if(user==null){
            throw new BusinessException("user not found");
        }


        List<ArticlesFeedResponse.ArticleBean> allList=
        articleMapper.getArticlesDTOAllListByuserId(user.getId());
        if(allList!=null){
            articlesFeedResponse.setArticlesCount(allList.size());
        }
        List<ArticlesFeedResponse.ArticleBean> list=
        articleMapper.getArticlesDTOListByuserId(user.getId(),limit,offset);
        if(list!=null&&!list.isEmpty()) {
            List<ArticlesFeedResponse.ArticleBean> articlesDTOList = list.stream().map(articlesDTO -> {
                Long articlesDTOId = articlesDTO.getId();
                articlesDTO.setTagList(new ArrayList<>());
                List<String> tagnameList = tagMapper.selectTagnameByarticleId(articlesDTOId);
                System.out.println("tagnameList" + tagnameList.toString());
                if (tagnameList != null && !tagnameList.isEmpty()) {
                    articlesDTO.setTagList(tagnameList);
                }
                Integer favoriteCount = Math.toIntExact(userFavoritesService.lambdaQuery().eq(UserFavorites::getArticleId, articlesDTOId).count());
                articlesDTO.setFavoritesCount(favoriteCount);
                //following必为true;
                Integer favorite =userFavoritesMapper.selectFavoritedBycurrentUsernameAndarticleId(articlesDTOId,currentUsername);
                if(favorite>0){
                    articlesDTO.setFavorited(true);
                }
                articlesDTO.setId(null);
                return articlesDTO;
            }).toList();
            if(articlesDTOList!=null){
                articlesFeedResponse.setArticles(articlesDTOList);
            }
        }
        return articlesFeedResponse;
    }


    private String generateSlug(String title) {
        if (StrUtil.isBlank(title)) {
            return "untitled";
        }

        String baseSlug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        if (StrUtil.isBlank(baseSlug)) {
            baseSlug = "untitled";
        }

        // 确保唯一性
        String slug = baseSlug;
        int counter = 1;
        while (this.lambdaQuery().eq(Article::getSlug, slug).exists()) {
            slug = baseSlug + "-" + (counter++);
        }
        return slug;
    }
}




