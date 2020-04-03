package com.kelab.experiment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
public class AppSetting {

    public static Long cacheMillisecond;

    @Value("${cache.millisecond}")
    public void setCacheMillisecond(Long cacheMillisecond) {
        AppSetting.cacheMillisecond = cacheMillisecond;
    }

}
