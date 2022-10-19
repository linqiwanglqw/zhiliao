package com.lin.vo;

import lombok.Data;

import java.util.List;

@Data
public class CommentVo {
    //防止前端 精度损失 把id转为string
//    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    private UserVo author;

    private String content;

    private List<CommentVo> childrens;

    private String createDate;

    private Integer level;

    private UserVo toUser;
}
