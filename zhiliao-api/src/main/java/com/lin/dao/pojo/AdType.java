package com.lin.dao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 广告类型对象 b_ad_type
 *
 * @author lqw
 * @date 2022-12-05
 */
@Data
public class AdType extends BaseEntity {

    private static final long serialVersionUID = 1L;


    /** 广告类型id */
    @TableId(type = IdType.ASSIGN_ID) // 默认id类型 （雪花算法）
    private Long adTyepId;

    /** 广告类型名称 */
    private String adTypeTitle;

    /** 广告（首部轮播广告、侧边广告、文档广告） */
    private String adTypeTag;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 状态（0正常 1停用） */
    private String status;

    public void setAdTyepId(Long adTyepId)
    {
        this.adTyepId = adTyepId;
    }

    public Long getAdTyepId()
    {
        return adTyepId;
    }
    public void setAdTypeTitle(String adTypeTitle)
    {
        this.adTypeTitle = adTypeTitle;
    }

    public String getAdTypeTitle()
    {
        return adTypeTitle;
    }
    public void setAdTypeTag(String adTypeTag)
    {
        this.adTypeTag = adTypeTag;
    }

    public String getAdTypeTag()
    {
        return adTypeTag;
    }
    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("adTyepId", getAdTyepId())
                .append("adTypeTitle", getAdTypeTitle())
                .append("adTypeTag", getAdTypeTag())
                .append("delFlag", getDelFlag())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }

}
