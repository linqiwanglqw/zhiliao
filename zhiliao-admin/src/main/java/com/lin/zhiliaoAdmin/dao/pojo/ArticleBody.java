package com.lin.zhiliaoAdmin.dao.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ArticleBody {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String content;

    private String contentHtml;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long articleId;
}
