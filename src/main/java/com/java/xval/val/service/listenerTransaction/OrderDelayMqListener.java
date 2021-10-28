package com.java.xval.val.service.listenerTransaction;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RocketMQMessageListener(topic = "topic-orderly", consumerGroup = "orderly-consumer-group", consumeMode = ConsumeMode.ORDERLY)
public class OrderDelayMqListener implements RocketMQListener<String> {


    @Override
    public void onMessage(String s) {
        log.info("#OrderMqListener接收到延时队列消息，开始消费.message={}:", s);
    }

}
