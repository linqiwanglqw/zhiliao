package com.lin.controller;

import com.lin.vo.Result;
import com.lin.common.aop.LogAnnotation;
import com.lin.common.cache.Cache;
import com.lin.service.ArticleService;
import com.lin.vo.params.ArticleParam;
import com.lin.vo.params.PageParams;
import com.lin.vo.params.PageSearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//json数据进行交互
@RestController
@RequestMapping("articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    /**
     * 首页 文章列表
     * @param pageParams
     * @return
     */
    @PostMapping
    //加上此注解 代表要对此接口记录日志
    @LogAnnotation(module="文章",operator="获取文章列表")
    @Cache(expire = 5 * 60 * 1000,name = "listArticle")
    public Result listArticle(@RequestBody PageParams pageParams){
        return articleService.listArticle(pageParams);
    }

    /**
     * 首页 文章列表
     * @param pageSearchParams
     * @return
     */
    @PostMapping("/search")
    //加上此注解 代表要对此接口记录日志
    @LogAnnotation(module="文章",operator="搜索文章列表")
    public Result listSearchArticle(@RequestBody PageSearchParams pageSearchParams){
        System.out.println(articleService.listSearchArticle(pageSearchParams));
        return articleService.listSearchArticle(pageSearchParams);
    }

    /**
     * 首页 最热文章
     * @return
     */
    @PostMapping("hot")
    @Cache(expire = 5 * 60 * 1000,name = "hot_article")
    public Result hotArticle(){
        int limit = 5;
        return articleService.hotArticle(limit);
    }

    /**
     * 首页 最新文章
     * @return
     */
    @PostMapping("new")
    @Cache(expire = 5 * 60 * 1000,name = "news_article")
    public Result newArticles(){
        int limit = 5;
        return articleService.newArticles(limit);
    }

    /**
     * 首页 文章归档
     * @return
     */
    @PostMapping("listArchives")
    public Result listArchives(){
        return articleService.listArchives();
    }


    @PostMapping("view/{id}")
    @Cache(expire = 5 * 60 * 1000,name = "view_article")
    public Result findArticleById(@PathVariable("id") Long articleId){
        System.out.println(articleService.findArticleById(articleId));
        return articleService.findArticleById(articleId);
    }
    //接口url：/articles/publish
    //
    //请求方式：POST

    /**
     * 写文章
     * @param articleParam
     * @return
     */
    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam){

        return articleService.publish(articleParam);
    }

    /**
     * 查看文章
     * @param articleId
     * @return
     */
    @PostMapping("{id}")
    public Result articleById(@PathVariable("id") Long articleId){
        return articleService.findArticleById(articleId);
    }
}
