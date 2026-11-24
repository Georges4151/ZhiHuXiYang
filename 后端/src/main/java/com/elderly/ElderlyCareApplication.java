package com.elderly;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.elderly.mapper")
@EnableScheduling
public class ElderlyCareApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElderlyCareApplication.class, args);
    }
}
