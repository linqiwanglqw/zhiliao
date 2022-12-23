package com.lin.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lin.dao.pojo.AdType;
import com.lin.vo.params.AdTypePageParam;
import com.lin.vo.params.PageParams;
import org.springframework.data.domain.Page;

/**
 * 广告类型Service接口
 *
 * @author lqw
 */
public interface AdTypeService {
    /**
     * 查询广告类型
     *
     * @param adTyepId 广告类型主键
     * @return 广告类型
     */
    public AdType selectAdTypeByAdTyepId(Long adTyepId);

    /**
     * 查询广告类型列表
     *
     * @param adType 广告类型
     * @return 广告类型集合
     */
    public IPage<AdType> selectAdTypeList(AdTypePageParam params);

    /**
     * 新增广告类型
     *
     * @param adType 广告类型
     * @return 结果
     */
    public int insertAdType(AdType adType);

    /**
     * 修改广告类型
     *
     * @param adType 广告类型
     * @return 结果
     */
    public int updateAdType(AdType adType);


    /**
     * 删除广告类型信息
     *
     * @param adTyepId 广告类型主键
     * @return 结果
     */
    public int deleteAdTypeByAdTyepId(Long adTyepId);
}
