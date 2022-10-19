package com.lin.zhiliaoAdmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lin.zhiliaoAdmin.dao.mapper.ArticleMapper;
import com.lin.zhiliaoAdmin.dao.mapper.CommentMapper;
import com.lin.zhiliaoAdmin.dao.mapper.CommentVOMapper;
import com.lin.zhiliaoAdmin.dao.mapper.SysUserMapper;
import com.lin.zhiliaoAdmin.dao.pojo.Article;
import com.lin.zhiliaoAdmin.dao.pojo.Comment;
import com.lin.zhiliaoAdmin.dao.pojo.SysUser;
import com.lin.zhiliaoAdmin.vo.CommentVO;
import com.lin.zhiliaoAdmin.vo.ErrorCode;
import com.lin.zhiliaoAdmin.vo.PageResult;
import com.lin.zhiliaoAdmin.vo.Result;
import com.lin.zhiliaoAdmin.vo.params.PageParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentVOMapper commentVOMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 评论列表
     * */
    public Result listComment(PageParam pageParam) {
        Page<Comment> page = new Page<>(pageParam.getCurrentPage(), pageParam.getPageSize());
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        // 如果搜索框内信息不为空
        if (StringUtils.isNotBlank(pageParam.getQueryString())) {
            // 根据搜索框内信息查询用户信息
            SysUser sysUser = selectSysUser(pageParam.getQueryString());
            // 根据搜索框内信息查询文章信息，因为文章标题不唯一，所以可能查询到多个文章，需要用集合来接收结果
            List<Article> articleList = selectArticle(pageParam.getQueryString());
            if (sysUser != null) {
                // 如果查询到用户信息，则根据用户ID进行查询；
                queryWrapper.eq(Comment::getAuthorId, sysUser.getId());
            } else if (!(articleList.isEmpty())) {
                // 查询到评论信息，根据文章id进行查询
                for (int i = 0; i < articleList.size(); i++) {
                    queryWrapper.eq(Comment::getArticleId, articleList.get(i).getId());
                }
            } else {
                // 上面条件都不满足，则最后根据评论内容查询
                queryWrapper.like(Comment::getContent, pageParam.getQueryString());
            }
        }
        Page<Comment> commentPage = this.commentMapper.selectPage(page, queryWrapper);
        PageResult<CommentVO> pageResult = new PageResult<>();
        pageResult.setList(copyList(commentPage.getRecords()));
        for (int i = 0; i < commentPage.getRecords().size(); i++) {

            // 查询文章类别名称，并添加
            Long articleId = commentPage.getRecords().get(i).getArticleId();
            Article article = selectArticleById(articleId);
            //存在空指针的情况
            if(article!=null){
                pageResult.getList().get(i).setArticleTitle(article.getTitle() + "(" + articleId + ")");
                // 查询评论人，并添加
                Long authorId = commentPage.getRecords().get(i).getAuthorId();
                SysUser sysUser = selectSysUserById(authorId);
                pageResult.getList().get(i).setAuthor(sysUser.getAccount());
            }

        }
        pageResult.setTotal(commentPage.getTotal());
        return Result.success(pageResult);
    }

    private List<CommentVO> copyList(List<Comment> records) {
        List<CommentVO> commentVOList = new ArrayList<>();
        for (Comment record : records) {
            commentVOList.add(copy(record));
        }
        return commentVOList;
    }

    private CommentVO copy(Comment comment) {
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment, commentVO);
        return commentVO;
    }

    /**
     * 根据文章id查询文章信息
     */
    public Article selectArticleById(Long articleId) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId, articleId);
        return this.articleMapper.selectOne(queryWrapper);
    }

    /**
     * 根据用户id查询用户信息
     */
    public SysUser selectSysUserById(Long sysUserId) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getId, sysUserId);
        return this.sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 根据用户名或者昵称查询用户信息
     */
    public SysUser selectSysUser(String username) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, username)
                .or()
                .eq(SysUser::getNickname, username);
        return this.sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 根据文章标题查询文章信息
     */
    public List<Article> selectArticle(String title) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getTitle, title);
        return this.articleMapper.selectList(queryWrapper);
    }

    /**
     * 写评论
     * */
    public Result add(CommentVO commentVO) {
        if (commentVO.getArticleId() == null || commentVO.getContent() == null) {
            return Result.fail(ErrorCode.MUST_NOT_BE_NULL.getCode(), ErrorCode.MUST_NOT_BE_NULL.getMsg());
        }
        int parentId = 0;
        Comment comment = new Comment();
        Article article = selectArticleById(commentVO.getArticleId());
        comment.setArticleId(commentVO.getArticleId());
        comment.setContent(commentVO.getContent());
        SysUser sysUser = selectSysUser(user());
        comment.setAuthorId(sysUser.getId());
        comment.setCreateDate(System.currentTimeMillis());
        comment.setLevel("1");
        if(article!=null){
            comment.setToUid(article.getAuthorId());
        }
        comment.setParentId((long) parentId);
        this.commentMapper.insert(comment);
        return Result.success(null);
    }

    /**
     * 编辑评论
     * */
    public Result update(Comment comment) {
        if (comment.getArticleId() == null || comment.getContent() == null) {
            return Result.fail(ErrorCode.MUST_NOT_BE_NULL.getCode(), ErrorCode.MUST_NOT_BE_NULL.getMsg());
        }
        int result = this.commentMapper.updateById(comment);
        if (result == 0) {
            return Result.fail(ErrorCode.COMMENTS_IS_NOT_EXIST.getCode(), ErrorCode.COMMENTS_IS_NOT_EXIST.getMsg());
        }
        return Result.success(null);
    }

    public Result delete(Long id) {
        this.commentMapper.deleteById(id);
        return Result.success(null);
    }

    /**
     * 获取当前登录用户信息
     * */
    public String user() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        if (principal instanceof Principal) {
            return ((Principal) principal).getName();
        }
        return String.valueOf(principal);
    }

}
