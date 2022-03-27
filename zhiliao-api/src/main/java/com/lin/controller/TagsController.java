package com.lin.controller;

import com.lin.service.TagService;
import com.lin.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tags")
public class TagsController {
    @Autowired
    private TagService tagService;

    /**
     * 最热标签
     * @return
     */
    //   /tags/hot
    @GetMapping("hot")
    public Result hot(){
        int limit = 6;
        return tagService.hots(limit);
    }

    /**
     * 写文章 获取文章标签
     * @return
     */
    @GetMapping
    public Result findAll(){
        return tagService.findAll();
    }

    /**
     * 查询标签详情（只有标签）
     * @return
     */
    @GetMapping("detail")
    public Result findAllDetail(){
        return tagService.findAllDetail();
    }

    /**
     * 查询标签列表
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    public Result findDetailById(@PathVariable("id") Long id){
        return tagService.findDetailById(id);
    }

}
