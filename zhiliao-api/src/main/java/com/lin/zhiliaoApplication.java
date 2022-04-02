package com.lin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  //该注解表名在启动定时任务
public class zhiliaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(zhiliaoApplication.class,args);
    }

}
