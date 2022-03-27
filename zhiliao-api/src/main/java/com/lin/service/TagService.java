package com.lin.service;

import com.lin.vo.Result;
import com.lin.vo.TagVo;

import java.util.List;

public interface TagService {

    /**
     * 查询对应文章的标签
     * @param articleId
     * @return
     */
    List<TagVo> findTagsByArticleId(Long articleId);

    /**
     * 最热标签
     * @param limit
     * @return
     */
    Result hots(int limit);

    /**
     * 查询所有的文章标签
     * @return
     */
    Result findAll();

    Result findAllDetail();

    Result findDetailById(Long id);
}
