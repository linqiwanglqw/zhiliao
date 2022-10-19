package com.lin.zhiliaoAdmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lin.zhiliaoAdmin.dao.mapper.*;
import com.lin.zhiliaoAdmin.dao.pojo.*;
import com.lin.zhiliaoAdmin.vo.ArticleVO;
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
public class ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 文章列表
     * */
    public Result listArticle(PageParam pageParam) {
        Page<Article> page = new Page<>(pageParam.getCurrentPage(), pageParam.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

        // 如果搜索框内信息不为空
        if (StringUtils.isNotBlank(pageParam.getQueryString())) {
            // 根据搜索框内信息查询用户信息
            SysUser sysUser = selectSysUser(pageParam.getQueryString());
            // 根据搜索框内信息查询类别信息
            Category category = selectCategory(pageParam.getQueryString());
            if (sysUser != null) {
                // 如果查询到用户信息，则根据用户ID进行查询；
                queryWrapper.eq(Article::getAuthorId, sysUser.getId());
            } else if (category != null) {
                // 如果查询到类别信息，则根据类别名称进行查询；
                queryWrapper.eq(Article::getCategoryId, category.getId());
            }else {
                // 反之根据文章标题进行模糊查询
                queryWrapper.like(Article::getTitle, pageParam.getQueryString());
            }
        }
        Page<Article> articlePage = this.articleMapper.selectPage(page, queryWrapper);
        PageResult<ArticleVO> pageResult = new PageResult<>();
        pageResult.setList(copyList(articlePage.getRecords()));
        for (int i = 0; i < articlePage.getRecords().size(); i++) {
            // 查询文章类别名称，并添加
            Long categoryId = articlePage.getRecords().get(i).getCategoryId();
            Category category = selectCategory(categoryId);
            pageResult.getList().get(i).setCategoryName(category.getCategoryName());
            // 查询文章作者名字，并添加
            Long authorId = articlePage.getRecords().get(i).getAuthorId();
            SysUser sysUser = selectSysUser(authorId);
            pageResult.getList().get(i).setAuthorName(sysUser.getAccount());
        }

        pageResult.setTotal(articlePage.getTotal());
        return Result.success(pageResult);
    }

    /**
     * 根据用户名或者昵称查询用户信息
     * */
    public SysUser selectSysUser (String username) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, username)
                .or()
                .eq(SysUser::getNickname, username);
        return this.sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 根据用户id查询用户信息
     * */
    public SysUser selectSysUser (Long id) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getId, id);
        return this.sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 根据类别名称查询类别信息
     * */
    public Category selectCategory (String categoryName) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getCategoryName, categoryName);
        return this.categoryMapper.selectOne(queryWrapper);
    }

    /**
     * 根据类别id查询类别信息
     * */
    public Category selectCategory (Long categoryId) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getId, categoryId);
        return this.categoryMapper.selectOne(queryWrapper);
    }

    private List<ArticleVO> copyList(List<Article> records) {
        List<ArticleVO> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record));
        }
        return articleVoList;
    }

    private ArticleVO copy(Article article) {
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        return articleVO;
    }

    /**
     * 撰写文章
     * */
    public Result add(ArticleVO articleVO) {
        if (articleVO.getTitle() == null || articleVO.getCategoryName() == null || articleVO.getSummary() == null || articleVO.getBody() == null) {
            return Result.fail(ErrorCode.MUST_NOT_BE_NULL.getCode(), ErrorCode.MUST_NOT_BE_NULL.getMsg());
        }
        Article article = new Article();
        // 根据类别名称查询类别信息
        Category category = selectCategory(articleVO.getCategoryName());
        if (category == null) {
            return Result.fail(ErrorCode.CATEGORY_IS_NOT_EXIST.getCode(), ErrorCode.CATEGORY_IS_NOT_EXIST.getMsg());
        }
        article.setCategoryId(category.getId());
        // 根据当前登录用户查询用户信息
        SysUser sysUser = selectSysUser(user());
        // 添加作者id
        article.setAuthorId(sysUser.getId());
        // 添加文章简介
        article.setSummary(articleVO.getSummary());
        // 向文章内容表中新增一条数据
        ArticleBody articleBody = new ArticleBody();
        articleBody.setContent(articleVO.getBody());
        this.articleBodyMapper.insert(articleBody);
        // 添加文章内容id
        article.setBodyId(articleBody.getId());
        article.setCreateDate(System.currentTimeMillis());
        // 添加文章标题
        article.setTitle(articleVO.getTitle());
        // 执行插入
        this.articleMapper.insert(article);
        // 根据文章内容id修改其文章id
        LambdaQueryWrapper<ArticleBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleBody::getId, article.getBodyId());
        articleBody.setArticleId(article.getId());
        this.articleBodyMapper.update(articleBody, queryWrapper);
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

    /**
     * 编辑文章
     * */
    public Result update(ArticleVO articleVO) {
        if (articleVO.getTitle() == null || articleVO.getCategoryName() == null || articleVO.getViewCounts() == null || articleVO.getCommentCounts() == null || articleVO.getAuthorName() == null) {
            return Result.fail(ErrorCode.MUST_NOT_BE_NULL.getCode(), ErrorCode.MUST_NOT_BE_NULL.getMsg());
        }
        // 根据类别名称查询类别信息
        Category category = selectCategory(articleVO.getCategoryName());
        if (category == null) {
            return Result.fail(ErrorCode.CATEGORY_IS_NOT_EXIST.getCode(), ErrorCode.CATEGORY_IS_NOT_EXIST.getMsg());
        }
        // 根据作者名字查询作者信息
        SysUser sysUser = selectSysUser(articleVO.getAuthorName());
        if (sysUser == null) {
                return Result.fail(ErrorCode.ACCOUNT_IS_NOT_EXIST.getCode(), ErrorCode.ACCOUNT_IS_NOT_EXIST.getMsg());
        }
        Article article = new Article();
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId, articleVO.getId());
        // 修改文章相关内容
        article.setTitle(articleVO.getTitle());
        article.setCategoryId(category.getId());
        article.setAuthorId(sysUser.getId());
        article.setCommentCounts(articleVO.getCommentCounts());
        article.setViewCounts(articleVO.getViewCounts());
        this.articleMapper.update(article, queryWrapper);
        return Result.success(null);
    }

    /**
     * 删除文章
     * */
    public Result delete(Long articleId) {
        this.articleMapper.deleteById(articleId);
        // 删除文章的同时将其下面的所有评论一并删除
        List<Comment> commentList = selectCommentsByArticleId(articleId);
        if (!(commentList.isEmpty())) {
            for (int i = 0; i < commentList.size(); i++) {
                this.commentMapper.deleteById(commentList.get(i).getId());
            }
        }
        // 文章主题内容一并删除
        ArticleBody articleBody = selectArticleBodyByArticleId(articleId);
        if (articleBody != null) {
            this.articleBodyMapper.deleteById(articleBody.getId());
        }
        return Result.success(null);
    }

    /**
     * 根据文章id查询其下的所有评论
     * */
    public List<Comment> selectCommentsByArticleId (Long articleId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId, articleId);
        return this.commentMapper.selectList(queryWrapper);
    }

    /**
     * 根据文章id查询其文章内容
     * */
    public ArticleBody selectArticleBodyByArticleId (Long articleId) {
        LambdaQueryWrapper<ArticleBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleBody::getArticleId, articleId);
        return this.articleBodyMapper.selectOne(queryWrapper);
    }
}
