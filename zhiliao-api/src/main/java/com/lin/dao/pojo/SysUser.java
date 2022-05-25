package com.lin.dao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class SysUser {

//    @TableId(type = IdType.ASSIGN_ID) // 默认id类型 （雪花算法）
//    @TableId(type = IdType.AUTO) 数据库自增
    private Long id;

    private String account;

    private Integer admin;

    private String avatar;

    private Long createDate;

    private Integer deleted;

    private String email;

    private Long lastLogin;

    private String mobilePhoneNumber;

    private String nickname;

    private String password;

    private String salt;

    private String status;

    private Long birthday;

    private String ip;
    /**
     * 性别 0；未知  1：男  2：女
     */
    private Long sex;
}
