package com.lin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lin.dao.pojo.Ad;
import com.lin.vo.params.AdTypePageParam;

import java.util.List;

/**
 * adService接口
 *
 * @author lqw
 * @date 2022-12-06
 */
public interface AdService {
    /**
     * 查询ad
     *
     * @param adId ad主键
     * @return ad
     */
    public Ad selectAdByAdId(Long adId);

    /**
     * 查询ad列表
     *
     * @param ad ad
     * @return ad集合
     */
    public IPage<Ad> selectAdList(AdTypePageParam ad);

    /**
     * 新增ad
     *
     * @param ad ad
     * @return 结果
     */
    public int insertAd(Ad ad);

    /**
     * 修改ad
     *
     * @param ad ad
     * @return 结果
     */
    public int updateAd(Ad ad);


    /**
     * 删除ad信息
     *
     * @param adId ad主键
     * @return 结果
     */
    public int deleteAdByAdId(Long adId);
}
