package com.kelab.experiment;

import cn.wzy.verifyUtils.annotation.EnableVerify;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableEurekaClient
@EnableVerify
@MapperScan(basePackages = "com.kelab.experiment.dal.dao")
@SpringBootApplication
@EnableFeignClients(basePackages = "com.kelab.experiment.support.facade")
public class ExperimentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExperimentApplication.class, args);
    }

}
