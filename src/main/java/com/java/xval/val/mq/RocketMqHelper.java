package com.java.xval.val.mq;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;


/**
 * 描述:
 * 〈RocketMq封装工具类〉
 *
 * @author fangyang
 * @since 2021-10-20
 */
public class RocketMqHelper implements SendCallback {

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
     * 发送异步消息
     *
     * @param topic   消息Topic
     * @param message 消息实体
     */
    public void asyncSend(Enum topic, Message<?> message) {
        asyncSend(topic.name(), message, getDefaultSendCallBack());
    }


    /**
     * 发送异步消息
     *
     * @param topic        消息Topic
     * @param message      消息实体
     * @param sendCallback 回调函数
     */
    public void asyncSend(Enum topic, Message<?> message, SendCallback sendCallback) {
        asyncSend(topic.name(), message, sendCallback);
    }

    /**
     * 发送异步消息
     *
     * @param topic   消息Topic
     * @param message 消息实体
     */
    public void asyncSend(String topic, Message<?> message) {
        rocketMQTemplate.asyncSend(topic, message, getDefaultSendCallBack());
    }

    /**
     * 发送异步消息
     *
     * @param topic        消息Topic
     * @param message      消息实体
     * @param sendCallback 回调函数
     */
    public void asyncSend(String topic, Message<?> message, SendCallback sendCallback) {
        rocketMQTemplate.asyncSend(topic, message, sendCallback);
    }

    /**
     * 发送异步消息
     *
     * @param topic        消息Topic
     * @param message      消息实体
     * @param sendCallback 回调函数
     * @param timeout      超时时间
     */
    public void asyncSend(String topic, Message<?> message, SendCallback sendCallback, long timeout) {
        rocketMQTemplate.asyncSend(topic, message, sendCallback, timeout);
    }

    /**
     * 发送异步消息
     *
     * @param topic        消息Topic
     * @param message      消息实体
     * @param sendCallback 回调函数
     * @param timeout      超时时间
     * @param delayLevel   延迟消息的级别
     */
    public void asyncSend(String topic, Message<?> message, SendCallback sendCallback, long timeout, int delayLevel) {
        rocketMQTemplate.asyncSend(topic, message, sendCallback, timeout, delayLevel);
    }

    /**
     * 发送顺序消息
     *
     * @param message 消息实体
     * @param topic   消息Topic
     * @param hashKey 为了保证到同一个队列中，将消息发送到orderTopic主题上
     *                他的hash值计算发送到哪一个队列，用的是同一个值,那么他们的hash一样就可以保证发送到同一个队列里
     */
    public void syncSendOrderly(Enum topic, Message<?> message, String hashKey) {
        syncSendOrderly(topic.name(), message, hashKey);
    }

    /**
     * 发送顺序消息
     *
     * @param message 消息实体
     * @param topic   消息Topic
     * @param hashKey 为了保证到同一个队列中，将消息发送到orderTopic主题上
     *                他的hash值计算发送到哪一个队列，用的是同一个值,那么他们的hash一样就可以保证发送到同一个队列里
     */
    public void syncSendOrderly(String topic, Message<?> message, String hashKey) {
        LOGGER.info("发送顺序消息，topic:" + topic + ",hashKey:" + hashKey);
        rocketMQTemplate.syncSendOrderly(topic, message, hashKey);
    }

    /**
     * 发送顺序消息
     *
     * @param message 消息实体
     * @param topic   消息Topic
     * @param hashKey 为了保证到同一个队列中，将消息发送到orderTopic主题上
     *                他的hash值计算发送到哪一个队列，用的是同一个值,那么他们的hash一样就可以保证发送到同一个队列里
     * @param timeout 延时时间
     */
    public void syncSendOrderly(String topic, Message<?> message, String hashKey, long timeout) {
        LOGGER.info("发送顺序消息，topic:" + topic + ",hashKey:" + hashKey + ",timeout:" + timeout);
        rocketMQTemplate.syncSendOrderly(topic, message, hashKey, timeout);
    }

    /**
     * 默认CallBack函数
     *
     * @return SendCallback
     */
    private SendCallback getDefaultSendCallBack() {
        return new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                LOGGER.info("send message success. topic=" + sendResult.getMessageQueue().getTopic() + ", msgId=" + sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable throwable) {
                LOGGER.error("send message failed.", throwable);
            }
        };
    }


    @PreDestroy
    public void destroy() {
        LOGGER.info("RocketMqHelper#RocketMq注销");
    }

    @Override
    public void onSuccess(SendResult sendResult) {
        // 消费发送成功
        LOGGER.info("send message success. topic=" + sendResult.getMessageQueue().getTopic() + ", msgId=" + sendResult.getMsgId());
    }

    @Override
    public void onException(Throwable throwable) {
        LOGGER.error("send message failed. =" + throwable);
    }
}
