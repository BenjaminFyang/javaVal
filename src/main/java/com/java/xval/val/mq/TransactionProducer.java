package com.java.xval.val.mq;

import com.java.xval.val.common.config.RocketMqDataConfig;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;

@Component
public class TransactionProducer {

    private TransactionMQProducer transactionMQProducer;

    @Resource
    private RocketMqDataConfig rocketMqDataConfig;

    /**
     * 启动监听器
     *
     * @param transactionListener 事务监听器
     * @param executorService     自定义线程池<根据不同的场景定义>
     */
    public void init(TransactionListener transactionListener, ExecutorService executorService) {
        transactionMQProducer = new TransactionMQProducer(rocketMqDataConfig.getOrderTopic());
        transactionMQProducer.setNamesrvAddr(rocketMqDataConfig.getNameServer());
        transactionMQProducer.setSendMsgTimeout(Integer.MAX_VALUE);
        transactionMQProducer.setExecutorService(executorService);
        transactionMQProducer.setTransactionListener(transactionListener);
        this.start();
    }

    /**
     * 启动
     * 对象在使用之前必须要调用一次，只能初始化一次
     */
    private void start() {
        try {
            this.transactionMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 事务消息发送
     *
     * @param data  消息发送对象.
     * @param topic 消息队列的主题.
     * @return the TransactionSendResult
     * @throws MQClientException 对应的异常的抛出.
     */
    public TransactionSendResult send(String data, String topic) throws MQClientException {
        Message message = new Message(topic, data.getBytes());
        return this.transactionMQProducer.sendMessageInTransaction(message, null);
    }
}
