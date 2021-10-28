package com.java.xval.val.common.config;

import com.java.xval.val.mq.RocketMQTemplateProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@ConditionalOnWebApplication
@Configuration(proxyBeanMethods = false)
public class RocketMqAutoConfiguration {

    @Bean
    public RocketMQTemplateProducer rocketMqHelper() {
        return new RocketMQTemplateProducer();
    }
}
