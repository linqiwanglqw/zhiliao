package com.lin.zhiliaoAdmin.vo;

public enum  ErrorCode {
    MUST_NOT_BE_NULL(400, "必填项信息不允许为空"),
    ACCOUNT_IS_EXIST(401, "用户名已存在"),
    EMAIL_EXIST(402, "该邮箱已注册"),
    CATEGORY_IS_NOT_EXIST(403, "无该分类"),
    CATEGORY_IS_EXIST(405, "该分类已存在"),
    ACCOUNT_IS_NOT_EXIST(406, "无该用户"),
    COMMENTS_IS_NOT_EXIST(407, "评论已被删除"),
    MUST_IS_LONG(408, "请输入正确的编号"),
    TAG_IS_EXIST(409, "该标签已存在"),
    NICKNAME_IS_EXIST(410, "该昵称已存在")
    ;

    private int code;
    private String msg;

    ErrorCode(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

