package com.lin.service.impl;

import com.lin.dao.pojo.SysUser;
import com.lin.service.LikeService;
import com.lin.utils.UserThreadLocal;
import com.lin.vo.params.LikeParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;



@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    RedisTemplate redisTemplate;
    @Override
    public Long findLikeNum(LikeParam likeParam) {
        //计算集合大小
        Long size = redisTemplate.opsForSet().size("articleId_"+likeParam.getArticleId());
        return size;
    }

    /**
     * 点赞后
     * @param likeParam
     * @return
     */
    @Override
    public boolean findLike(LikeParam likeParam) {
        boolean bl;
        boolean flag = redisTemplate.opsForSet().isMember("articleId_"+likeParam.getArticleId(),"user_"+likeParam.getAccount());
        if (flag)
        {
            redisTemplate.opsForSet().remove("articleId_"+likeParam.getArticleId(),"user_"+likeParam.getAccount());
            bl = false;
        }else {
            redisTemplate.opsForSet().add("articleId_"+likeParam.getArticleId(),"user_"+likeParam.getAccount());
            bl = true;
        }
        return bl;
    }

    /**
     * 初始查询
     */
    @Override
    public boolean selectLike(LikeParam likeParam) {
        boolean flag = redisTemplate.opsForSet().isMember("articleId_"+likeParam.getArticleId(),"user_"+likeParam.getAccount());
        return flag;
    }
}
