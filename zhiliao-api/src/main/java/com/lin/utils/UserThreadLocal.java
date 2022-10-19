package com.lin.utils;

import com.lin.dao.pojo.SysUser;

public class UserThreadLocal {

    private UserThreadLocal() {
    }

    //线程变量隔离
    private static final ThreadLocal<SysUser> LOCAL = new ThreadLocal<>();

    //存user
    public static void put(SysUser sysUser) {
        LOCAL.set(sysUser);
    }

    //取user
    public static SysUser get() {
        return LOCAL.get();
    }

    //移除user
    public static void remove() {
        LOCAL.remove();
    }
}
