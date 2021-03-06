package com.pp.seckilltest;

import com.spring4all.swagger.EnableSwagger2Doc;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@EnableSwagger2Doc
@SpringBootApplication
@MapperScan("com.pp.seckilltest.mapper")
public class SeckillTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillTestApplication.class, args);
        log.info("项目启动");
    }

}
