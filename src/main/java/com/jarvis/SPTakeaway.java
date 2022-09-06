package com.jarvis;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.annotation.ServletSecurity;

@Slf4j
@SpringBootApplication
@ServletComponentScan //扫描过滤器的filter
@EnableTransactionManagement //开启事务支持
@EnableCaching //开启缓存注解功能
public class SPTakeaway {

    public static void main(String[] args) {
        SpringApplication.run(SPTakeaway.class, args);
            log.info("SPTakeaway Will Start ...");
    }
}
