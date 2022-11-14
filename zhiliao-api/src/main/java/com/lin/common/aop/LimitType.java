package com.lin.common.aop;

public enum LimitType {
    /**
     * 默认策略全局限流，例如：A接口1分钟内允许访问100次
     */
    DEFAULT,

    /**
     * 根据请求者IP进行限流，例如：ip地址A可以在1分钟内访问接口50次
     */
    IP
}
