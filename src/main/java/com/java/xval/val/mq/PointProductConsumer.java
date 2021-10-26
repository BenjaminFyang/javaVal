package com.java.xval.val.mq;


import com.java.xval.val.common.config.RocketMqDataConfig;
import com.java.xval.val.service.listenerTransaction.PointTransactionListener;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


/**
 * 订单消费者监听
 */
@Component
public class PointProductConsumer {

    @Resource
    private RocketMqDataConfig rocketMqDataConfig;

    @Resource
    private PointTransactionListener orderListener;

    @PostConstruct
    public void init() throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(MqConstant.ConsumeGroup.USER_ORDER_GROUP);
        defaultMQPushConsumer.setNamesrvAddr(rocketMqDataConfig.getNameServer());
        defaultMQPushConsumer.subscribe(MqConstant.Top.USER_ORDER_TOPIC, "*");
        defaultMQPushConsumer.registerMessageListener(orderListener);
        defaultMQPushConsumer.start();
    }

}
