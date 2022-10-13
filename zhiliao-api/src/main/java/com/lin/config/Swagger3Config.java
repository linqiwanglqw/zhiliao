package com.lin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

@Configuration
public class Swagger3Config {

    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.OAS_30)
                .groupName("文章业务组")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.lin.controller"))//选择指定路径的api
                .build()
                .apiInfo(createApiInfo())
                .enable(true);//正式环境必须为false
    }
    @Bean
    public ApiInfo createApiInfo(){
        return new ApiInfo("zhiliao Swagger",
                "zhiliao Api Documentation",
                "3.0",
                "http:zhiliao.com",
                new Contact("小林","http:zhiliao.com","@qq.com"),
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<>());
    }

    @Bean
    public Docket createRestApi2(){
        return new Docket(DocumentationType.OAS_30)
                .groupName("登录业务组")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.lin.controller"))//选择指定路径的api
                .build()
                .apiInfo(createApiInfo2())
                .enable(true);//正式环境必须为false
    }
    @Bean
    public ApiInfo createApiInfo2(){
        return new ApiInfo("zhiliao Swagger",
                "zhiliao Api Documentation",
                "3.0",
                "http:zhiliao.com",
                new Contact("晓棋","http:zhiliao.com","@qq.com"),
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<>());
    }
}
