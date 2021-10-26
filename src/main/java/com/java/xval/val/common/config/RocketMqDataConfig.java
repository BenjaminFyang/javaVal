package com.java.xval.val.common.config;


import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class RocketMqDataConfig {

    /**
     * 配置中心读取 服务器地址
     */
//    @Value("${name_server}")
    private String nameServer = "localhost:9876";

    /**
     * 配置中心读取 主题
     */
//    @Value("${order_topic}")
    private String orderTopic = "order_trans_group";
}
