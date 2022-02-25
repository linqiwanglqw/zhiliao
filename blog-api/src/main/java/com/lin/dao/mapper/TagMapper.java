package com.lin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lin.dao.pojo.Tag;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {
    /**
     * 根据文章id查询标签列表
     * @param articleId
     * @return
     */
    List<Tag> findTagsByArticleId(Long articleId);

    /**
     * 查询最热的标签的id 前n条
     * @param limit
     * @return
     */
    List<Long> findHotsTagIds(int limit);

    /**
     * 根据id返回最热标签名称
     * @param tagIds
     * @return
     */
    List<Tag> findTagsByTagIds(List<Long> tagIds);
}
