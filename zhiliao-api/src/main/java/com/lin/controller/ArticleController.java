package com.lin.controller;

import com.lin.common.aop.LimitType;
import com.lin.common.aop.RateLimiter;
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

import java.io.IOException;

//json数据进行交互
@RestController
@RequestMapping("articles")
@Api(tags = "文章数据业务")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 限流接口
     */
    @RateLimiter(time = 3, count = 1, limitType = LimitType.IP)
    @GetMapping("/test/{id}")
    @LogAnnotation(module = "文章", operator = "test接口")
    public String test(@PathVariable("id") String id) {
        System.out.println(id);
        return "ok";
    }

    @PostMapping
    //加上此注解 代表要对此接口记录日志
//    @LogAnnotation(module = "文章", operator = "获取文章列表接口")
    @Cache(expire = 1 * 60 * 1000, name = "listArticle")
    @ApiOperation("按年月份查询文章列表接口")
    public Result listArticle(@RequestBody PageParams pageParams) {
        return articleService.listArticle(pageParams);
    }

    @ApiOperation("查询文章搜索列表接口")
    @GetMapping("/search")
    //加上此注解 代表要对此接口记录日志
//    @LogAnnotation(module = "文章", operator = "搜索文章列表")
    public Result listSearchArticle(@RequestBody PageSearchParams pageSearchParams) {
        return articleService.listSearchArticle(pageSearchParams);
    }

    //表头的查询
    @ApiOperation("查询文章搜索列表接口")
    @PostMapping("/headerSearch")
    //加上此注解 代表要对此接口记录日志
//    @LogAnnotation(module = "文章", operator = "搜索文章列表")
    public Result HeaderSearchArticle(@RequestBody ArticleParam articleParam) {
        String search = articleParam.getSearch();
        return articleService.searchArticle(search);
    }

    @GetMapping("hot")
    @ApiOperation("查询首页 最热文章接口")
    @Cache(expire = 5 * 60 * 1000, name = "hot_article")
    public Result hotArticle() {
        int limit = 5;
        return articleService.hotArticle(limit);
    }

    @GetMapping("new")
    @ApiOperation("查询首页 最新文章接口")
    @Cache(expire = 1 * 60 * 1000, name = "news_article")
    public Result newArticles() {
        int limit = 5;
        return articleService.newArticles(limit);
    }

    @GetMapping("listArchives")
    @ApiOperation("查询首页 文章归档接口")
    public Result listArchives() {
        return articleService.listArchives();
    }


    @GetMapping("view/{id}")
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
    @ApiOperation("新增修改文章接口")
    @RateLimiter(time = 10, count = 1, limitType = LimitType.IP)
    @LogAnnotation(module = "文章", operator = "新增修改文章接口")
    public Result publish(@RequestBody ArticleParam articleParam) throws IOException {
        return articleService.publish(articleParam);
    }

    @PostMapping("{id}")
    @ApiOperation("查看文章编辑数据接口")
    public Result articleById(@PathVariable("id") Long articleId) {
        return articleService.findArticleById(articleId);
    }

    @DeleteMapping("delArticle/{id}")
    @ApiOperation("删除文章编辑数据接口")
    @LogAnnotation(module = "文章", operator = "删除文章接口")
    public Result delArticleById(@PathVariable("id") Long articleId) {
        return Result.success(articleService.delAticleById(articleId));
    }
}
