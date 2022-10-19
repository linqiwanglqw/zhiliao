package com.lin.zhiliaoAdmin.dao.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class Article {

    public static final int Article_TOP = 1;

    public static final int Article_Common = 0;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String title;

    private Integer commentCounts;

    private Integer viewCounts;

    /**
     * 作者id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long authorId;

    /**
     *类别id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    private String summary;

    private Long bodyId;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Long createDate;
}

