package com.lin.controller;

import com.lin.common.aop.LogAnnotation;
import com.lin.vo.Result;
import com.lin.service.CommentsService;
import com.lin.vo.params.CommentParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comments")
public class CommentsController {
    @Autowired
    private CommentsService commentsService;

    /**
     * 评论
     *
     * @param id
     * @return
     */
    @GetMapping("article/{id}")
    public Result comments(@PathVariable("id") Long id) {
        return commentsService.commentsByArticleId(id);
    }


    @PostMapping("create/change")
    @LogAnnotation(module = "评论", operator = "添加评论接口")
    public Result comment(@RequestBody CommentParam commentParam) {
        return commentsService.comment(commentParam);
    }
}
