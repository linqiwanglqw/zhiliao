package com.lin.service.impl;

import com.lin.service.LikeService;
import com.lin.vo.params.LikeParam;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public Long findLikeNum(LikeParam likeParam) {
        //计算集合大小
        Long size = redisTemplate.opsForSet().size("articleId_" + likeParam.getArticleId());
        return size;
    }

    /**
     * 点赞后
     *
     * @param likeParam
     * @return
     */
    @Override
    public boolean findLike(LikeParam likeParam) {
        boolean bl;
        boolean flag = redisTemplate.opsForSet().isMember("articleId_" + likeParam.getArticleId(), likeParam.getUserId());
        if (flag) {
            redisTemplate.opsForSet().remove("articleId_" + likeParam.getArticleId(), likeParam.getUserId());
            bl = false;
        } else {
            redisTemplate.opsForSet().add("articleId_" + likeParam.getArticleId(), likeParam.getUserId());
            bl = true;
        }
        return bl;
    }

    /**
     * 初始查询
     */
    @Override
    public boolean selectLike(LikeParam likeParam) {
        boolean flag = redisTemplate.opsForSet().isMember("articleId_" + likeParam.getArticleId(), likeParam.getUserId());
        return flag;
    }
}
