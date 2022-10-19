package com.lin.zhiliaoAdmin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.lin.zhiliaoAdmin.dao"})
public class ZhiliaoAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhiliaoAdminApplication.class, args);
    }

}
