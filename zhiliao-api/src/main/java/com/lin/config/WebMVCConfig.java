package com.lin.config;

import com.lin.handler.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //跨域配置
        registry.addMapping("/**")
                .allowedMethods("DELETE", "GET", "POST", "OPTIONS", "PUT")
                .allowedOrigins("http://localhost:8080");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截test接口，后续实际遇到需要拦截的接口时，在配置为真正的拦截接口
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/test")
                .addPathPatterns("/comments/create/change")
                .addPathPatterns("/articles/publish")
                .addPathPatterns("/like/islike")
                .addPathPatterns("/users/findUserByAccount/*")//根据账号查找用户信息
                .addPathPatterns("/users/updateUser")//修改用户信息
                .addPathPatterns("addInterceptors/**");//点赞接口
    }
}
