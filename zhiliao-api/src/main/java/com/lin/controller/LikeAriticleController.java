package com.lin.controller;

import com.lin.service.LikeService;
import com.lin.vo.Result;
import com.lin.vo.params.LikeParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("like")
public class LikeAriticleController {

    @Autowired
    LikeService likeService;

    /**
     * 查询当前点赞个数
     * @param likeParam
     * @return
     */
    @PostMapping("num")
    public Result like(@RequestBody LikeParam likeParam){
        Long num= likeService.findLikeNum(likeParam);
        return Result.success(num);
    }

    /**
     * 改变点赞状态 要文章id，用户id
     * @param likeParam
     * @return
     */
    @PostMapping("islike")
    public Result islike(@RequestBody LikeParam likeParam){
        boolean flag = likeService.findLike(likeParam);
        return Result.success(flag);
    }

    /**
     * 查询点赞初始状态 要文章id，用户id
     * @param
     * @return
     */
    @PostMapping("initlike")
    public Result initlike(@RequestBody LikeParam likeParam){
        boolean flag = likeService.selectLike(likeParam);
        return Result.success(flag);
    }
}
