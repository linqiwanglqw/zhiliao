package com.lin.vo.params;

import lombok.Data;

@Data
public class AdTypePageParam {
    private int page = 1;

    private int pageSize = 10;


    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 状态（0正常 1停用） */
    private String status;
}
