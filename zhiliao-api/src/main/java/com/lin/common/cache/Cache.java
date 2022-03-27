package com.lin.common.cache;


import java.lang.annotation.*;

//以下是元注解

/**
 * @author linqiwang
 */
//target注解的作用表示可以注解作用的范围
@Target({ElementType.METHOD})//作用域是在方法上
//retention表示什么时候有效  其中runtime包括class包括sources（源码级别）
@Retention(RetentionPolicy.RUNTIME)//运行时有效
//Documented表示是否将我们的注解生成在Javadoc中
@Documented
public @interface Cache {

    //缓存时间
    long expire() default 1 * 60 * 1000;
    //缓存标识 key
    String name() default "";

}
