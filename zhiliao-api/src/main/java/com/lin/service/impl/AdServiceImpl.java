package com.lin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lin.dao.mapper.AdMapper;
import com.lin.dao.pojo.Ad;
import com.lin.dao.pojo.AdType;
import com.lin.service.AdService;
import com.lin.utils.DateUtils;
import com.lin.utils.SnowFlakeUtil;
import com.lin.vo.params.AdTypePageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * adService业务层处理
 *
 * @author lqw
 */
@Service
public class AdServiceImpl implements AdService {
    @Autowired
    private AdMapper adMapper;

    /**
     * 查询ad
     *
     * @param adId ad主键
     * @return ad
     */
    @Override
    public Ad selectAdByAdId(Long adId) {
        return adMapper.selectAdByAdId(adId);
    }

    /**
     * 查询ad列表
     *
     * @param params
     * @return ad
     */
    @Override
    public IPage<Ad> selectAdList(AdTypePageParam params) {
        //页号
        int page = params.getPage();
        //每页条数
        int pageSize = params.getPageSize();
        //使用ipage分页
        IPage<AdType> iPage = new Page<>(page, pageSize);
        Ad ad = new Ad();
        ad.setStatus(params.getStatus());
        ad.setDelFlag(params.getDelFlag());
        return adMapper.selectAdList(iPage,ad);
    }

    /**
     * 新增ad
     *
     * @param ad ad
     * @return 结果
     */
    @Override
    public int insertAd(Ad ad) {
        ad.setCreateTime(DateUtils.getNowDate());
        ad.setAdId(SnowFlakeUtil.getNextId());
        int rows = adMapper.insertAd(ad);
        return rows;
    }

    /**
     * 修改ad
     *
     * @param ad ad
     * @return 结果
     */
    @Override
    public int updateAd(Ad ad) {
        ad.setUpdateTime(DateUtils.getNowDate());
        return adMapper.updateAd(ad);
    }

    /**
     * 删除ad信息
     *
     * @param adId ad主键
     * @return 结果
     */
    @Override
    public int deleteAdByAdId(Long adId) {
        return adMapper.deleteAdByAdId(adId);
    }

}
