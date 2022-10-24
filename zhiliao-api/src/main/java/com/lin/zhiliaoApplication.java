package com.lin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@EnableScheduling  //该注解表名在启动定时任务
@EnableOpenApi //开启swagger   http://localhost:8160/swagger-ui/index.html
public class zhiliaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(zhiliaoApplication.class, args);
    }

}
