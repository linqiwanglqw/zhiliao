package com.lin.dao.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lin.dao.pojo.Ad;
import com.lin.dao.pojo.AdType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * adMapper接口
 * @author lqw
 */
public interface AdMapper {
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
    public IPage<Ad> selectAdList(IPage<AdType> page,@Param("ad") Ad ad);

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
     * 删除ad
     *
     * @param adId ad主键
     * @return 结果
     */
    public int deleteAdByAdId(Long adId);



    /**
     * 通过广告类型信息删除同类型广告
     *
     * @param adId adID
     * @return 结果
     */
    public int deleteAdTypeByAdTyepId(Long adId);
}
