package com.lin.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lin.dao.pojo.Ad;
import com.lin.dao.pojo.AdType;
import com.lin.service.AdService;
import com.lin.vo.Result;
import com.lin.vo.params.AdTypePageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * adController
 *
 * @author lqw
 */
@Controller
@RequestMapping("/ads")
public class AdController {

    @Autowired
    private AdService adService;

    /**
     * 查询广告类型列表
     */
    @GetMapping("/{adId}")
    @ResponseBody
    public Result getAdTypeId(@PathVariable Long adId) {
        Ad ad = adService.selectAdByAdId(adId);
        return Result.success(ad);
    }


    /**
     * 查询ad列表
     */
    @PostMapping("/adList")
    @ResponseBody
    public Result list(AdTypePageParam ad) {
        IPage<Ad> ads = adService.selectAdList(ad);
        return Result.success(ads);
    }


    /**
     * 新增保存ad
     */
    @PostMapping("/ad")
    @ResponseBody
    public Result addSave(Ad ad) {
        return Result.success(adService.insertAd(ad));
    }


    /**
     * 修改保存ad
     */
    @PutMapping("/ad")
    @ResponseBody
    public Result editSave(Ad ad) {
        return Result.success(adService.updateAd(ad));
    }

    /**
     * 删除ad
     */
    @DeleteMapping("/{adID}")
    @ResponseBody
    public Result remove(@PathVariable Long adID) {
        return Result.success(adService.deleteAdByAdId(adID));
    }
}
