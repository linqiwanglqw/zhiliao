package com.lin.zhiliaoAdmin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ArticleVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String title;



    private Integer commentCounts;

    private Integer viewCounts;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Long createDate;

    /**
     * 作者
     */
    private String authorName;

    private String summary;

    private String body;

    private String categoryName;

}
