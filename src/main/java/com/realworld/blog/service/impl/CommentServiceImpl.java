package com.realworld.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.realworld.blog.common.BusinessException;
import com.realworld.blog.dto.request.CreateCommentRequest;
import com.realworld.blog.dto.response.DeleteCommentResponse;
import com.realworld.blog.dto.response.CreateCommentResponse;
import com.realworld.blog.dto.response.ListCommentsResponse;
import com.realworld.blog.entity.Article;
import com.realworld.blog.entity.Comment;
import com.realworld.blog.entity.User;
import com.realworld.blog.entity.UserFollows;
import com.realworld.blog.interceptor.JwtInterceptor;
import com.realworld.blog.mapper.ArticleMapper;
import com.realworld.blog.service.ArticleService;
import com.realworld.blog.service.CommentService;
import com.realworld.blog.mapper.CommentMapper;
import com.realworld.blog.service.UserFollowsService;
import com.realworld.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
* @author jiaolei
* @description 针对表【comment】的数据库操作Service实现
* @createDate 2026-06-08 19:43:30
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserFollowsService userFollowsService;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserService userService;



    @Override
    public ListCommentsResponse listComments(String slug) {
        //res1+following
        ListCommentsResponse listCommentsResponse = new ListCommentsResponse();
        listCommentsResponse.setComments(new ArrayList<>());

        String currentUsername = JwtInterceptor.getCurrentUser();
        if(slug==null){
            throw new BusinessException("slug is empty");
        }

        Article article = articleService.lambdaQuery().eq(Article::getSlug, slug).one();
        if(article==null){
            throw new BusinessException("article not found");
        }
//        List<CommentsListResponse.CommentsBean> commentsBeanList=commentMapper.getCommentBeanBySlug(slug);
        List<ListCommentsResponse.CommentsBean> commentsBeanList=commentMapper.getCommentBeanByArticleId(article.getId());
        if(commentsBeanList!=null){
            listCommentsResponse.setComments(commentsBeanList);
            if(currentUsername!=null){
                User user = userService.lambdaQuery().eq(User::getUsername, currentUsername).one();
                if(user==null){
                    throw new BusinessException("user not found");
                }
                List<ListCommentsResponse.CommentsBean> list = commentsBeanList.stream().filter(commentsBean -> (commentsBean != null && commentsBean.getAuthor() != null))
                        .map(commentsBean -> {

                            Long followeeId = commentsBean.getAuthor().getUserId();
                            Long followerId = user.getId();
                            if (followerId!=followeeId&&userFollowsService.lambdaQuery()
                                    .eq(UserFollows::getFolloweeId, followeeId)
                                    .eq(UserFollows::getFollowerId, followerId).exists()) {
                                commentsBean.getAuthor().setFollowing(true);
                            }
                            commentsBean.getAuthor().setUserId(null);
                            return commentsBean;
                        }).toList();
                if(list!=null){
                    listCommentsResponse.setComments(list);
                }
            }

        }
        return listCommentsResponse;
    }

    @Override
    public CreateCommentResponse createComment(CreateCommentRequest createCommentRequest, String slug) {
        //insert into comment (body,article_id,author_id)
        // (#{body},(select id from article a where a.slug=#{slug}),(select u.id from user u where u.username=#{currentUsername}))
        //select *,u.id,u.username,u.bio,u.image a.articleAuthorId from comment c join user u on u.id=c.authorId join article a on a.id=c.article.id
        //where u.username=#{currentUsername} and a.slug=#{slug}
        //select userFollow(c.authorId,c.articleId)
        // res1+following
        CreateCommentResponse createCommentResponse = new CreateCommentResponse();
        createCommentResponse.setComment(new CreateCommentResponse.CommentBean());
        createCommentResponse.getComment().setAuthor(new CreateCommentResponse.CommentBean.AuthorBean());

        String currentUsername = JwtInterceptor.getCurrentUser();
        if(StrUtil.isBlank(currentUsername)){
            throw new BusinessException("user not logged in");
        }
        if(StrUtil.isBlank(slug)){
            throw new BusinessException("slug is empty");
        }
        String body = createCommentRequest.getComment().getBody();
        if(StrUtil.isBlank(body)){
            throw new BusinessException("comment is empty");
        }
        Comment comment = new Comment();

//        Boolean inserted=commentMapper.insertCommentBySlugAndUsernameAndBody(slug,currentUsername,body);
        Article article = articleService.lambdaQuery().eq(Article::getSlug, slug).one();
        if(article==null){
            throw new BusinessException("article not found");
        }
        User user = userService.lambdaQuery().eq(User::getUsername, currentUsername).one();
        if(user==null){
            throw new BusinessException("user not found");
        }
        comment.setArticleId(article.getId());
        comment.setBody(body);
        comment.setAuthorId(user.getId());
        boolean saved = this.save(comment);
        if(!saved){
            throw new BusinessException("insert failed");
        }
//        CommentsCreateResponse.CommentBean commentBean=commentMapper.getCommentBean(currentUsername,slug);
        CreateCommentResponse.CommentBean commentBean=commentMapper.getCommentBeanByCommentId(comment.getId());
        System.out.println(commentBean.toString());
        if(commentBean==null){
            throw new BusinessException("comment not found");
        }
        UserFollows userFollows = userFollowsService.lambdaQuery().eq(UserFollows::getFolloweeId, article.getAuthorId())
                .eq(UserFollows::getFollowerId, commentBean.getAuthor().getUserId()).one();
        if(userFollows!=null){
            commentBean.getAuthor().setFollowing(true);
        }else{
            commentBean.getAuthor().setFollowing(false);
        }

        createCommentResponse.setComment(commentBean);
        return createCommentResponse;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @Override
    public DeleteCommentResponse deleteComment(String slug, Long id) {
        //
        String currentUsername = JwtInterceptor.getCurrentUser();
        if(StrUtil.isBlank(currentUsername)){
            throw new BusinessException("user not logged in");
        }
        User user = userService.lambdaQuery().eq(User::getUsername, currentUsername).one();
        if(user==null){
            throw new BusinessException("user not found");
        }
        if(StrUtil.isBlank(slug)){
            throw new BusinessException("slug is empty");
        }
        Article article = articleService.lambdaQuery().eq(Article::getSlug, slug).one();
        if(article==null){
            throw new BusinessException("article not found");
        }
        Comment comment = this.lambdaQuery().eq(Comment::getId, id).eq(Comment::getArticleId, article.getId()).one();
        if(comment==null){
            throw new BusinessException("comment not found");
        }
        if(comment.getAuthorId()!=user.getId()){
            throw new BusinessException("permission denied");
        }
        boolean removed = this.removeById(comment.getId());
        if(!removed){
            throw new BusinessException("deleted failed");
        }
        return null;
    }
}




