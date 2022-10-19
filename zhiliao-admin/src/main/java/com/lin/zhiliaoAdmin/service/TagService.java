package com.lin.zhiliaoAdmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lin.zhiliaoAdmin.dao.mapper.ArticleMapper;
import com.lin.zhiliaoAdmin.dao.mapper.ArticleTagMapper;
import com.lin.zhiliaoAdmin.dao.mapper.TagMapper;
import com.lin.zhiliaoAdmin.dao.pojo.Article;
import com.lin.zhiliaoAdmin.dao.pojo.ArticleTag;
import com.lin.zhiliaoAdmin.dao.pojo.Tag;
import com.lin.zhiliaoAdmin.vo.ErrorCode;
import com.lin.zhiliaoAdmin.vo.PageResult;
import com.lin.zhiliaoAdmin.vo.Result;
import com.lin.zhiliaoAdmin.vo.params.PageParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserService userService;

    /**
     * 标签列表
     * */
    public Result listTag(PageParam pageParam) {
        Page<Tag> page = new Page<>(pageParam.getCurrentPage(), pageParam.getPageSize());
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(pageParam.getQueryString()), Tag::getTagName, pageParam.getQueryString());
        Page<Tag> permissionPage = this.tagMapper.selectPage(page, queryWrapper);

        PageResult<Tag> pageResult = new PageResult<>();
        pageResult.setList(permissionPage.getRecords());
        pageResult.setTotal(permissionPage.getTotal());
        return Result.success(pageResult);
    }

    /**
     * 添加标签
     * */
    public Result add(Tag tag) {
        if (tag == null) {
            return Result.fail(ErrorCode.MUST_NOT_BE_NULL.getCode(), ErrorCode.MUST_NOT_BE_NULL.getMsg());
        }
        // 校验输入的标签名称是否已经存在，如果存在则提示已存在，不要重复添加
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getTagName, tag.getTagName());
        Tag result = this.tagMapper.selectOne(queryWrapper);
        if (result != null) {
            if (tag.getTagName().equals(result.getTagName())) {
                return Result.fail(ErrorCode.TAG_IS_EXIST.getCode(), ErrorCode.TAG_IS_EXIST.getMsg());
            }
        }
        tag.setCreator(user());
        tag.setCreateDate(System.currentTimeMillis());
        tag.setAvatar("/static/tag/java.png");
        this.tagMapper.insert(tag);
        return Result.success(null);
    }

    /**
     * 编辑标签
     * */
    public Result update(Tag tag) {
        if (tag == null) {
            return Result.fail(ErrorCode.MUST_NOT_BE_NULL.getCode(), ErrorCode.MUST_NOT_BE_NULL.getMsg());
        }
        this.tagMapper.updateById(tag);
        return Result.success(null);
    }

    /**
     * 删除标签
     * */
    public Result delete(Long id) {
        this.tagMapper.deleteById(id);
        LambdaQueryWrapper<ArticleTag> query = new LambdaQueryWrapper<>();
        query.eq(ArticleTag::getTagId, id);
        List<ArticleTag> articleTagList = this.articleTagMapper.selectList(query);
        if (!(articleTagList.isEmpty())) {
            for (int i = 0; i < articleTagList.size(); i++) {
                Article article = selectArticleByArticleId(articleTagList.get(i).getArticleId());
                userService.deleteArticleCommentArticleBody(article);
            }
            for (int i = 0; i < articleTagList.size(); i++) {
                this.articleTagMapper.deleteById(articleTagList.get(i).getId());
            }
        }
        return Result.success(null);
    }

    /**
     * 根据文章id查询文章信息
     * */
    public Article selectArticleByArticleId(Long id) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId, id);
        return this.articleMapper.selectOne(queryWrapper);
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
