package com.java.xval.val.service.impl;

import com.alibaba.fastjson.JSON;
import com.java.xval.val.common.api.ResultCode;
import com.java.xval.val.common.exception.ApiException;
import com.java.xval.val.mapper.OrderMapper;
import com.java.xval.val.mapper.TransactionLogMapper;
import com.java.xval.val.model.Order;
import com.java.xval.val.model.TransactionLog;
import com.java.xval.val.mq.MqConstant;
import com.java.xval.val.mq.OrderTransactionProducer;
import com.java.xval.val.service.OrderService;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 订单业务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private TransactionLogMapper transactionLogMapper;

    @Resource
    private OrderTransactionProducer orderTransactionProducer;

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Order order, String transactionId) {

        LOGGER.info("OrderServiceImpl开始进行下单的操作={},transactionId={}", JSON.toJSONString(order), transactionId);

        // 1、本应用创建订单
        orderMapper.create(order);

        // 2.写入事务日志
        TransactionLog log = new TransactionLog();
        log.setId(transactionId);
        log.setBusiness(MqConstant.Top.USER_ORDER_TOPIC);
        log.setForeignKey(String.valueOf(order.getId()));
        transactionLogMapper.insert(log);
        logger.info("OrderServiceImpl订单创建完成={}", order);
    }

    @Override
    public void createOrder(Order order) throws MQClientException {
        TransactionSendResult transactionSendResult = orderTransactionProducer.send(JSON.toJSONString(order), MqConstant.Top.USER_ORDER_TOPIC);
        SendStatus sendStatus = transactionSendResult.getSendStatus();
        if (!sendStatus.equals(SendStatus.SEND_OK)) {
            throw new ApiException(ResultCode.FAILED);
        }
    }
}
