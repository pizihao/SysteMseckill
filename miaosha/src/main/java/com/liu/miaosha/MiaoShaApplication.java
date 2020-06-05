package com.liu.miaosha;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author shidacaizi
 * @date 2020/6/3 11:14
 */
@SpringBootApplication
@MapperScan("com.liu.miaosha.mapper")
public class MiaoShaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiaoShaApplication.class, args);
    }
}
