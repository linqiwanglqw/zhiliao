package com.lin.dao.pojo;

import java.util.List;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * ad对象 b_ad
 *
 * @author lqw
 * @date 2022-12-06
 */
public class Ad extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 广告id
     */
    private Long adId;

    /**
     * 广告类型id
     */
    private Long adTypeId;

    /**
     * 广告标题
     */
    private String adTitle;

    /**
     * 广告url
     */
    private String adUrl;

    /**
     * 广告排序
     */
    private String adSort;

    /**
     * 广告开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date adBeginTime;

    /**
     * 广告结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date adEndTime;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 广告类型信息
     */
    private List<AdType> adTypeList;

    public void setAdId(Long adId) {
        this.adId = adId;
    }

    public Long getAdId() {
        return adId;
    }

    public void setAdTypeId(Long adTypeId) {
        this.adTypeId = adTypeId;
    }

    public Long getAdTypeId() {
        return adTypeId;
    }

    public void setAdTitle(String adTitle) {
        this.adTitle = adTitle;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdSort(String adSort) {
        this.adSort = adSort;
    }

    public String getAdSort() {
        return adSort;
    }

    public void setAdBeginTime(Date adBeginTime) {
        this.adBeginTime = adBeginTime;
    }

    public Date getAdBeginTime() {
        return adBeginTime;
    }

    public void setAdEndTime(Date adEndTime) {
        this.adEndTime = adEndTime;
    }

    public Date getAdEndTime() {
        return adEndTime;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public List<AdType> getAdTypeList() {
        return adTypeList;
    }

    public void setAdTypeList(List<AdType> adTypeList) {
        this.adTypeList = adTypeList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("adId", getAdId())
                .append("adTypeId", getAdTypeId())
                .append("adTitle", getAdTitle())
                .append("adUrl", getAdUrl())
                .append("adSort", getAdSort())
                .append("adBeginTime", getAdBeginTime())
                .append("adEndTime", getAdEndTime())
                .append("delFlag", getDelFlag())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("adTypeList", getAdTypeList())
                .toString();
    }
}
