package com.lin.dao.pojo;

import lombok.Data;

@Data
public class OpenaiAnswers {
    private Long id;

    private String articleId;

    private String answer;
}
