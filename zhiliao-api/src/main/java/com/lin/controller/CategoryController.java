package com.lin.controller;

import com.lin.vo.Result;
import com.lin.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("categorys")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 写文章 获取文章分类
     *
     * @return
     */
    @GetMapping
    public Result categories() {
        return categoryService.findAll();
    }


    /**
     * 获取导航栏分类信息
     *
     * @return
     */
    @GetMapping("detail")
    public Result categoriesDetail() {
        return categoryService.findAllDetail();
    }

    ///category/detail/{id}

    /**
     * 显示分类详情
     *
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    public Result categoryDetailById(@PathVariable("id") Long id) {
        return categoryService.categoryDetailById(id);
    }
}
