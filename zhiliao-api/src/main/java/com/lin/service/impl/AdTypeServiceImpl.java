package com.lin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lin.dao.mapper.AdMapper;
import com.lin.dao.mapper.AdTypeMapper;
import com.lin.dao.pojo.AdType;
import com.lin.service.AdTypeService;
import com.lin.utils.SnowFlakeUtil;
import com.lin.utils.text.ConvertUtils;
import com.lin.utils.DateUtils;
import com.lin.vo.params.AdTypePageParam;
import com.lin.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 广告类型Service业务层处理
 *
 * @author lqw
 */
@Service
public class AdTypeServiceImpl implements AdTypeService {
    @Autowired
    private AdTypeMapper adTypeMapper;

    private AdMapper adMapper;

    /**
     * 查询广告类型
     *
     * @param adTyepId 广告类型主键
     * @return 广告类型
     */
    @Override
    public AdType selectAdTypeByAdTyepId(Long adTyepId) {
        return adTypeMapper.selectAdTypeByAdTyepId(adTyepId);
    }

    /**
     * 查询广告类型列表
     *
     * @return 广告类型
     */
    @Override
    public IPage<AdType> selectAdTypeList(AdTypePageParam params) {

        //页号
        int page = params.getPage();
        //每页条数
        int pageSize = params.getPageSize();
        //使用ipage分页
        IPage<AdType> iPage = new Page<>(page, pageSize);
        AdType adType = new AdType();
        adType.setStatus(params.getStatus());
        adType.setDelFlag(params.getDelFlag());
        adTypeMapper.selectAdTypeList(iPage, adType);

        return iPage;
    }

    /**
     * 新增广告类型
     *
     * @param adType 广告类型
     * @return 结果
     */
    @Override
    public int insertAdType(AdType adType) {
        adType.setCreateTime(DateUtils.getNowDate());
        //雪花算法
        adType.setAdTyepId(SnowFlakeUtil.getNextId());
        return adTypeMapper.insertAdType(adType);
    }

    /**
     * 修改广告类型
     *
     * @param adType 广告类型
     * @return 结果
     */
    @Override
    public int updateAdType(AdType adType) {
        adType.setUpdateTime(DateUtils.getNowDate());
        return adTypeMapper.updateAdType(adType);
    }

    /**
     * 删除广告类型信息
     *
     * @param adTyepId 广告类型主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteAdTypeByAdTyepId(Long adTyepId) {
        //删除广告
        adMapper.deleteAdTypeByAdTyepId(adTyepId);
        //删除广告类型
        return adTypeMapper.deleteAdTypeByAdTyepId(adTyepId);
    }
}
