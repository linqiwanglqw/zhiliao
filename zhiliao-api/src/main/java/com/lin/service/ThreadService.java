package com.lin.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lin.dao.mapper.ArticleMapper;
import com.lin.dao.pojo.Article;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThreadService {

    //期望此操作在线程池 执行 不会影响原有的主线程、用于更新阅读数
    @Async("taskExecutor")
    public void updateArticleViewCount(ArticleMapper articleMapper, Article article) {

        int viewCounts = article.getViewCounts();
        Article articleUpdate = new Article();
        articleUpdate.setViewCounts(viewCounts + 1);
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, article.getId());
        //设置一个 为了在多线程的环境下 线程安全
        updateWrapper.eq(Article::getViewCounts, viewCounts);
        // update article set view_count=100 where view_count=99 and id=11  版本控制
        articleMapper.update(articleUpdate, updateWrapper);
//        try {
//            Thread.sleep(5000);
//            System.out.println("更新完成了....");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
