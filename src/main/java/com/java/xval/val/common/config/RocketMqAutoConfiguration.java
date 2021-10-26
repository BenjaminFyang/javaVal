package com.java.xval.val.common.config;

import com.java.xval.val.mq.RocketMqHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@ConditionalOnWebApplication
@Configuration(proxyBeanMethods = false)
public class RocketMqAutoConfiguration {

    @Bean
    public RocketMqHelper rocketMqHelper() {
        return new RocketMqHelper();
    }
}
