package com.lin.service.mq;

import com.alibaba.fastjson.JSON;
import com.lin.service.ArticleService;
import com.lin.vo.ArticleMessage;
import com.lin.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;


@Slf4j
@Component
@RocketMQMessageListener(topic = "api-update-article",consumerGroup = "api-update-article-group")
public class ArticleListener implements RocketMQListener<ArticleMessage> {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(ArticleMessage articleMessage) {
       log.info("收到的消息：{}",articleMessage);
        //1. 更新查看文章详情的缓存
        Long articleId = articleMessage.getArticleId();
        String params = DigestUtils.md5Hex(articleId.toString());
        String redisKey = "view_article::ArticleController::findArticleById::"+params;
        //查询文章数据，从线程中修改阅读数，并发挥对应vo
        Result articleResult = articleService.findArticleById(articleId);
        //修改对应key的values的值
        redisTemplate.opsForValue().set(redisKey,JSON.toJSONString(articleResult), Duration.ofMillis(5 * 60 * 1000));
        log.info("更新了缓存:{}",redisKey);
        //2. 文章列表的缓存 不知道参数,解决办法 直接删除缓存
        Set<String> keys = redisTemplate.keys("listArticle*");
        keys.forEach(s -> {
            redisTemplate.delete(s);
            log.info("删除了文章列表的缓存:{}",s);
        });
    }
}
