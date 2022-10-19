package com.lin.zhiliaoAdmin.controller;

import com.lin.zhiliaoAdmin.dao.pojo.*;
import com.lin.zhiliaoAdmin.service.*;
import com.lin.zhiliaoAdmin.vo.ArticleVO;
import com.lin.zhiliaoAdmin.vo.CommentVO;
import com.lin.zhiliaoAdmin.vo.Result;
import com.lin.zhiliaoAdmin.vo.params.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TagService tagService;

    // 查询权限
    @PostMapping("permission/permissionList")
    public Result permissionList(@RequestBody PageParam pageParam) {
        return permissionService.listPermission(pageParam);
    }

    // 添加权限
    @PostMapping("permission/add")
    public Result add(@RequestBody Permission permission) {
        return permissionService.add(permission);
    }

    // 修改权限
    @PostMapping("permission/update")
    public Result update(@RequestBody Permission permission) {
        return permissionService.update(permission);
    }

    // 删除权限
    @GetMapping("permission/delete/{id}")
    public Result delete(@PathVariable("id") Long id) {
        return permissionService.delete(id);
    }

    // 通过Spring Security获取当前登录用户名
    @PostMapping("user/user")
    public String user() {
        // 获取当前登录用户信息

            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
            if (principal instanceof Principal) {
                return ((Principal) principal).getName();
            }
            return String.valueOf(principal);

    }

    // 查询用户
    @PostMapping("user/userList")
    public Result userList(@RequestBody PageParam pageParam) {
        return userService.listUser(pageParam);
    }

    // 用户添加
    @PostMapping("user/add")
    public Result add(@RequestBody Admin admin) {
        return userService.add(admin);
    }

    // 用户修改
    @PostMapping("user/update")
    public Result update(@RequestBody Admin admin) {
        return userService.update(admin);
    }

    // 用户删除
    @GetMapping("user/delete/{id}")
    public Result deleteUser(@PathVariable("id") Long id) {
        return userService.delete(id);
    }

    // 查询文章
    @PostMapping("article/articleList")
    public Result articleList(@RequestBody PageParam pageParam) {
        return articleService.listArticle(pageParam);
    }

    // 文章撰写
    @PostMapping("article/add")
    public Result add(@RequestBody ArticleVO articleVO) {
        return articleService.add(articleVO);
    }

    // 文章修改
    @PostMapping("article/update")
    public Result update(@RequestBody ArticleVO article) {
        return articleService.update(article);
    }

    // 文章删除
    @GetMapping("article/delete/{id}")
    public Result deleteArticle(@PathVariable("id") Long id) {
        return articleService.delete(id);
    }

    // 分类查询
    @PostMapping("category/categoryList")
    public Result categoryList(@RequestBody PageParam pageParam) {
        return categoryService.listCategories(pageParam);
    }

    // 添加分类
    @PostMapping("category/add")
    public Result add(@RequestBody Category category) {
        return categoryService.add(category);
    }

    // 修改分类
    @PostMapping("category/update")
    public Result update(@RequestBody Category category) {
        return categoryService.update(category);
    }

    // 删除分类
    @GetMapping("category/delete/{id}")
    public Result deleteCategory(@PathVariable("id") Long id) {
        return categoryService.delete(id);
    }

    // 查询评论
    @PostMapping("comment/commentList")
    public Result commentList(@RequestBody PageParam pageParam) {
        return commentService.listComment(pageParam);
    }

    // 添加评论
    @PostMapping("comment/add")
    public Result add(@RequestBody CommentVO commentVO) {
        return commentService.add(commentVO);
    }

    // 修改评论
    @PostMapping("comment/update")
    public Result update(@RequestBody Comment comment) {
        return commentService.update(comment);
    }

    // 删除评论
    @GetMapping("comment/delete/{id}")
    public Result deleteComment(@PathVariable("id") Long id) {
        return commentService.delete(id);
    }

    // 查询权限
    @PostMapping("tag/tagList")
    public Result tagList(@RequestBody PageParam pageParam) {
        return tagService.listTag(pageParam);
    }

    // 添加权限
    @PostMapping("tag/add")
    public Result add(@RequestBody Tag tag) {
        return tagService.add(tag);
    }

    // 修改权限
    @PostMapping("tag/update")
    public Result update(@RequestBody Tag tag) {
        return tagService.update(tag);
    }

    // 删除权限
    @GetMapping("tag/delete/{id}")
    public Result deleteTag(@PathVariable("id") Long id) {
        return tagService.delete(id);
    }

    // 查询权限
    @PostMapping("sysUser/sysUserList")
    public Result sysUserList(@RequestBody PageParam pageParam) {
        return sysUserService.listSysUser(pageParam);
    }

    // 添加权限
    @PostMapping("sysUser/add")
    public Result add(@RequestBody SysUser sysUser) {
        return sysUserService.add(sysUser);
    }

    // 修改权限
    @PostMapping("sysUser/update")
    public Result update(@RequestBody SysUser sysUser) {
        return sysUserService.update(sysUser);
    }

    // 删除权限
    @GetMapping("sysUser/delete/{id}")
    public Result deleteSysUser(@PathVariable("id") Long id) {
        return sysUserService.delete(id);
    }
}

