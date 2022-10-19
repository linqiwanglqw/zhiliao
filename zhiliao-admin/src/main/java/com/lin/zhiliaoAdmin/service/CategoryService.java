package com.lin.zhiliaoAdmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lin.zhiliaoAdmin.dao.mapper.ArticleBodyMapper;
import com.lin.zhiliaoAdmin.dao.mapper.ArticleMapper;
import com.lin.zhiliaoAdmin.dao.mapper.CategoryMapper;
import com.lin.zhiliaoAdmin.dao.mapper.CommentMapper;
import com.lin.zhiliaoAdmin.dao.pojo.Article;
import com.lin.zhiliaoAdmin.dao.pojo.Category;
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
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserService userService;

    /**
     * 分类列表
     * */
    public Result listCategories(PageParam pageParam) {
        Page<Category> page = new Page<>(pageParam.getCurrentPage(), pageParam.getPageSize());
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(pageParam.getQueryString()), Category::getCategoryName, pageParam.getQueryString());
        Page<Category> permissionPage = this.categoryMapper.selectPage(page, queryWrapper);
        PageResult<Category> pageResult = new PageResult<>();
        pageResult.setList(permissionPage.getRecords());
        pageResult.setTotal(permissionPage.getTotal());
        return Result.success(pageResult);
    }

    /**
     * 添加分类
     * */
    public Result add(Category category) {
        if (category.getCategoryName() == null || category.getDescription() == null) {
            return Result.fail(ErrorCode.MUST_NOT_BE_NULL.getCode(), ErrorCode.MUST_NOT_BE_NULL.getMsg());
        }
        Result result = checkCategoryName(category.getCategoryName());
        if (!(result.isSuccess())) {
            return result;
        }
        category.setCreator(user());
        category.setAvatar("/static/category/language.png");
        category.setCreateDate(System.currentTimeMillis());
        this.categoryMapper.insert(category);
        return Result.success(null);
    }

    /**
     * 根据类别名称查询类别信息
     * */
    public Category selectCategoryByName(String name) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getCategoryName, name);
        return this.categoryMapper.selectOne(queryWrapper);
    }

    /**
     * 编辑分类信息
     * */
    public Result update(Category category) {
        // 校验必填项是否为空
        if (category.getCategoryName() == null || category.getDescription() == null) {
            return Result.fail(ErrorCode.MUST_NOT_BE_NULL.getCode(), ErrorCode.MUST_NOT_BE_NULL.getMsg());
        }
        // 根据前端传过来的类别id查询类别名称如果与前端传过来的类别名称相同则不做类别名称校验
        Category category1 = this.categoryMapper.selectById(category.getId());
        if (!(category1.getCategoryName().equals(category.getCategoryName()))) {
            Result result = checkCategoryName(category.getCategoryName());
            if (!(result.isSuccess())) {
                return result;
            }
        }
        this.categoryMapper.updateById(category);
        return Result.success(null);
    }

    /**
     * 校验类别信息
     * */
    public Result checkCategoryName(String categoryName) {
        Category category = selectCategoryByName(categoryName);
        // 如果类别已存在返回错误信息
        if (category != null) {
            return Result.fail(ErrorCode.CATEGORY_IS_EXIST.getCode(), ErrorCode.CATEGORY_IS_EXIST.getMsg());
        }
        return Result.success(null);
    }

    /**
     * 删除文章
     * */
    public Result delete(Long id) {
        this.categoryMapper.deleteById(id);
        List<Article> articleList = selectArticleByCategoryId(id);
        userService.deleteArticleCommentArticleBody(articleList);
        return Result.success(null);
    }

    /**
     * 根据类别id查询与之关联的所有文章
     * */
    public List<Article> selectArticleByCategoryId (Long categoryId) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getCategoryId, categoryId);
        return this.articleMapper.selectList(queryWrapper);
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
