package com.lin.controller;

import com.lin.vo.Result;
import com.lin.common.aop.LogAnnotation;
import com.lin.common.cache.Cache;
import com.lin.service.ArticleService;
import com.lin.vo.params.ArticleParam;
import com.lin.vo.params.PageParams;
import com.lin.vo.params.PageSearchParams;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

//json数据进行交互
@RestController
@RequestMapping("articles")
@Api(tags = "文章数据业务")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    @PostMapping
    //加上此注解 代表要对此接口记录日志
    @LogAnnotation(module = "文章", operator = "获取文章列表接口")
    @Cache(expire = 1 * 60 * 1000, name = "listArticle")
    @ApiOperation("查询文章列表接口")
    public Result listArticle(@RequestBody PageParams pageParams) {
        return articleService.listArticle(pageParams);
    }

    @ApiOperation("查询文章搜索列表接口")
    @PostMapping("/search")
    //加上此注解 代表要对此接口记录日志
    @LogAnnotation(module = "文章", operator = "搜索文章列表")
    public Result listSearchArticle(@RequestBody PageSearchParams pageSearchParams) {
        return articleService.listSearchArticle(pageSearchParams);
    }

    //表头的查询
    @ApiOperation("查询文章搜索列表接口")
    @PostMapping("/headerSearch")
    //加上此注解 代表要对此接口记录日志
    @LogAnnotation(module = "文章", operator = "搜索文章列表")
    public Result HeaderSearchArticle(@RequestBody ArticleParam articleParam) {
        String search = articleParam.getSearch();
        return articleService.searchArticle(search);
    }

    @PostMapping("hot")
    @Cache(expire = 5 * 60 * 1000, name = "hot_article")
    @ApiOperation("查询首页 最热文章接口")
    public Result hotArticle() {
        int limit = 5;
        return articleService.hotArticle(limit);
    }

    @PostMapping("new")
    @ApiOperation("查询首页 最新文章接口")
    @Cache(expire = 1 * 60 * 1000, name = "news_article")
    public Result newArticles() {
        int limit = 5;
        return articleService.newArticles(limit);
    }

    @PostMapping("listArchives")
    @ApiOperation("查询首页 文章归档接口")
    public Result listArchives() {
        return articleService.listArchives();
    }


    @PostMapping("view/{id}")
    @Cache(expire = 1 * 60 * 1000, name = "view_article")
    @ApiOperation("查询文章详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "文章id", required = true, paramType = "path", dataType = "Long")
    })
    @ApiResponses({
            @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对"),
            @ApiResponse(code = 520, message = "系统维护中")
    })
    public Result findArticleById(@PathVariable("id") Long articleId) {
        return articleService.findArticleById(articleId);
    }


    @PostMapping("publish")
    @ApiOperation("新增文章接口")
    public Result publish(@RequestBody ArticleParam articleParam) {
        return articleService.publish(articleParam);
    }

    @PostMapping("{id}")
    @ApiOperation("查看文章编辑数据接口")
    public Result articleById(@PathVariable("id") Long articleId) {
        return articleService.findArticleById(articleId);
    }

    @DeleteMapping("delArticle/{id}")
    @CrossOrigin
    @ApiOperation("删除文章编辑数据接口")
    public Result delArticleById(@PathVariable("id") Long articleId) {
        return Result.success(articleService.delAticleById(articleId));
    }
}
