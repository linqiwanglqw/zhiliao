package com.lin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lin.dao.pojo.AdType;
import com.lin.service.AdTypeService;
import com.lin.vo.Result;
import com.lin.vo.params.AdTypePageParam;
import com.lin.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 广告类型Controller
 *
 * @author lqw
 */
@Controller
@RequestMapping("/adTypes")
public class AdTypeController {

    @Autowired
    private AdTypeService adTypeService;


    /**
     * 查询广告类型列表
     */
    @GetMapping("/{adTypeId}")
    @ResponseBody
    public Result getAdTypeId(@PathVariable Long adTypeId) {
        AdType adType = adTypeService.selectAdTypeByAdTyepId(adTypeId);
        return Result.success(adType);
    }


    /**
     * 查询广告类型列表
     */
    @PostMapping("/list")
    @ResponseBody
    //@RequestBody只能一个
    public Result getAsTypeList(@RequestBody AdTypePageParam params) {
        IPage<AdType> adTypeIPage = adTypeService.selectAdTypeList(params);
        return Result.success(adTypeIPage);
    }


    /**
     * 新增保存广告类型
     */
    @PostMapping("/adType")
    @ResponseBody
    public Result addSave(AdType adType) {
        return Result.success(adTypeService.insertAdType(adType));
    }


    /**
     * 修改保存广告类型
     */
    @PutMapping("/")
    @ResponseBody
    public Result editSave(AdType adType) {
        return Result.success(adTypeService.updateAdType(adType));
    }

    /**
     * 删除广告类型
     */
    @DeleteMapping("/{adTypeId}")
    @ResponseBody
    public Result remove(@PathVariable Long adTypeId) {
        return Result.success(adTypeService.deleteAdTypeByAdTyepId(adTypeId));
    }
}
