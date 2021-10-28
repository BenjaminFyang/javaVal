package com.java.xval.val.mq;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;


/**
 * 描述:
 * 〈RocketMq封装工具类〉
 *
 * @author fangyang
 * @see https://ojuejin.cn/post/6850418115382738958
 * @since 2021-10-20
 */
public class RocketMqHelper {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqHelper.class);

    /**
     * rocketmq模板注入
     */
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @PostConstruct
    public void init() {
        LOGGER.info("RocketMq开始实例化");
    }

    /**
     * 发送普通消息
     *
     * @param topic   topic
     * @param message 消息体
     */
    public void sendMessage(String topic, Object message) {
        this.rocketMQTemplate.convertAndSend(topic, message);
        LOGGER.info("普通消息发送完成：message = {}", message);
    }

    /**
     * 发送同步消息
     *
     * @param topic   topic
     * @param message 消息体
     */
    public void syncSendMessage(String topic, Object message) {
        SendResult sendResult = this.rocketMQTemplate.syncSend(topic, message);
        LOGGER.info("同步发送消息完成：message = {}, sendResult = {}", message, sendResult);
    }

    /**
     * 发送异步消息
     *
     * @param topic   topic
     * @param message 消息体
     */
    public void asyncSendMessage(String topic, Object message) {
        this.rocketMQTemplate.asyncSend(topic, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                LOGGER.info("异步消息发送成功，message = {}, SendStatus = {}", message, sendResult.getSendStatus());
            }

            @Override
            public void onException(Throwable e) {
                LOGGER.info("异步消息发送异常，exception = {}", e.getMessage());
            }
        });
    }

    /**
     * 发送单向消息
     *
     * @param topic   topic
     * @param message 消息体
     */
    public void sendOneWayMessage(String topic, Object message) {
        this.rocketMQTemplate.sendOneWay(topic, message);
        LOGGER.info("单向发送消息完成：message = {}", message);
    }

    /**
     * 同步发送批量消息
     *
     * @param topic       topic
     * @param messageList 消息集合
     * @param timeout     超时时间（毫秒）
     */
    public void syncSendMessages(String topic, List<Message<?>> messageList, long timeout) {
        this.rocketMQTemplate.syncSend(topic, messageList, timeout);
        LOGGER.info("同步发送批量消息完成：message = {}", JSON.toJSONString(messageList));
    }

    /**
     * 发送事务消息
     *
     * @param topic   topic
     * @param message 消息对象
     */
    public void sendMessageInTransaction(String topic, Object message) {
        String transactionId = UUID.randomUUID().toString();
        TransactionSendResult result = this.rocketMQTemplate.sendMessageInTransaction(topic, MessageBuilder.withPayload(message)
                .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                .build(), message);
        LOGGER.info("发送事务消息（半消息）完成：result = {}", result);
    }

    /**
     * 发送携带 tag 的消息（过滤消息）
     *
     * @param topic   topic，RocketMQTemplate将 topic 和 tag 合二为一了，底层会进行
     *                拆分再组装。只要在指定 topic 时跟上 {:tags} 就可以指定tag
     *                例如 test-topic:tagA
     * @param message 消息体
     */
    public void syncSendMessageWithTag(String topic, Object message) {
        this.rocketMQTemplate.syncSend(topic, message);
        LOGGER.info("发送带 tag 的消息完成：message = {}", message);
    }

    /**
     * 同步发送延时消息
     *
     * @param topic      topic
     * @param message    消息体
     * @param timeout    超时
     * @param delayLevel 延时等级：现在RocketMq并不支持任意时间的延时，需要设置几个固定的延时等级，
     *                   从1s到2h分别对应着等级 1 到 18，消息消费失败会进入延时消息队列
     *                   "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
     */
    public void syncSendDelay(String topic, Object message, long timeout, int delayLevel) {
        this.rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(message).build(), timeout, delayLevel);
        LOGGER.info("已同步发送延时消息 message = {}", message);
    }

    /**
     * 异步发送延时消息
     *
     * @param topic      topic
     * @param message    消息对象
     * @param timeout    超时时间
     * @param delayLevel 延时等级
     */
    public void asyncSendDelay(String topic, Object message, long timeout, int delayLevel) {
        this.rocketMQTemplate.asyncSend(topic, MessageBuilder.withPayload(message).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                LOGGER.info("异步发送延时消息成功，message = {}", message);
            }

            @Override
            public void onException(Throwable throwable) {
                LOGGER.error("异步发送延时消息发生异常，exception = {}", throwable.getMessage());
            }
        }, timeout, delayLevel);
        LOGGER.info("已异步发送延时消息 message = {}", message);
    }

    /**
     * 发送单向顺序消息
     *
     * @param topic topic
     */
    public void sendOneWayOrderly(String topic) {
        for (int i = 0; i < 30; i++) {
            this.rocketMQTemplate.sendOneWayOrderly(topic, MessageBuilder.withPayload("message - " + i).build(), "topic");
            LOGGER.info("单向顺序发送消息完成：message = {}", "message - " + i);
        }
    }

    /**
     * 同步发送顺序消息
     *
     * @param topic topic
     */
    public void syncSendOrderly(String topic) {
        for (int i = 0; i < 30; i++) {
            SendResult sendResult = this.rocketMQTemplate.syncSendOrderly(topic, MessageBuilder.withPayload("message - " + i).build(), "syncOrderlyKey");
            LOGGER.info("同步顺序发送消息完成：message = {}, sendResult = {}", "message - " + i, sendResult);
        }
    }


}
