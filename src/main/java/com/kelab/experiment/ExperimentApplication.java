package com.kelab.experiment;

import cn.wzy.verifyUtils.annotation.EnableVerify;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@EnableVerify
@MapperScan(basePackages = "com.kelab.usercenter.dal.dao")
@SpringBootApplication
public class ExperimentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExperimentApplication.class, args);
    }

}
