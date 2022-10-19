package com.lin.service;

import com.lin.vo.Result;
import com.lin.vo.params.ArticleParam;
import com.lin.vo.params.PageParams;
import com.lin.vo.params.PageSearchParams;

public interface ArticleService {
    /**
     * 分页查询 文章列表
     *
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

    /**
     * 分页查询 文章列表
     *
     * @param pageSearchParams
     * @return
     */
    Result listSearchArticle(PageSearchParams pageSearchParams);

    /**
     * 最热文章
     *
     * @param limit
     * @return
     */
    Result hotArticle(int limit);

    /**
     * 最新文章
     *
     * @param limit
     * @return
     */
    Result newArticles(int limit);

    /**
     * 文章归档
     *
     * @return
     */
    Result listArchives();

    /**
     * 查看文章详情
     *
     * @param articleId
     * @return
     */
    Result findArticleById(Long articleId);

    /**
     * 文章发布服务
     *
     * @param articleParam
     * @return
     */
    Result publish(ArticleParam articleParam);

    Result delAticleById(Long articleId);

    Result searchArticle(String search);
}
