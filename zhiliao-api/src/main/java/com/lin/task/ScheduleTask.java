package com.lin.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lin.dao.mapper.ArticleMapper;
import com.lin.dao.pojo.Article;
import com.lin.service.impl.LikeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;


@Component
@Slf4j
public class ScheduleTask {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    private ArticleMapper articleMapper;

//    @Scheduled(cron = "0/3 * * * * ? ")
    public void redisLikeDataToMysql(){
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//        log.info("time:{},开始执行Redis数据持久化到MySQL任务", df.format(new Date()));
//        //根据articleId_查找key
//        Set<String> keys = redisTemplate.keys("articleId_*");
//        assert keys != null;
//        for (String key : keys) {
//            //切割articleId_，留下有效的文章id
//            String[] articleIdKeys = key.split("articleId_");
//            //文章id
//            String articleIdKey = articleIdKeys[1];
//            BoundSetOperations<String, String> setOperations = redisTemplate.boundSetOps(key);
//            Article articleUpdate = new Article();
//            //setOperations.size()点赞数
//            articleUpdate.setLikeUserCount(setOperations.size());
//            LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
//            //文章id相等的时候
//            updateWrapper.eq(Article::getBodyId,articleIdKey);
//            articleMapper.update(articleUpdate,updateWrapper);
            //集合
//            System.out.println(setOperations.members());
//        }
    }
}
