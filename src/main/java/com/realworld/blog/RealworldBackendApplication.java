package com.realworld.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.realworld.blog.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RealworldBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealworldBackendApplication.class, args);
    }

}
