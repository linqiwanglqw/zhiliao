package com.lin.dao.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lin.dao.pojo.AdType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 广告类型Mapper接口
 *
 * @author lqw
 */
public interface AdTypeMapper
{
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
    public IPage<AdType> selectAdTypeList(IPage<AdType> page,@Param("adType") AdType adType);

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
     * 删除广告类型
     *
     * @param adTyepId 广告类型主键
     * @return 结果
     */
    public int deleteAdTypeByAdTyepId(Long adTyepId);

}
