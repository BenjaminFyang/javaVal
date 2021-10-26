package com.java.xval.val.service;


import com.java.xval.val.model.Order;
import org.apache.rocketmq.client.exception.MQClientException;

public interface OrderService {

    /**
     * 创建订单 消息队列封装调用.
     *
     * @param order         订单对象.
     * @param transactionId 对应的事务的id.
     */
    void create(Order order, String transactionId);

    /**
     * 创建订单.
     *
     * @param order 订单对象
     * @throws MQClientException 异常
     */
    void createOrder(Order order) throws MQClientException;
}
