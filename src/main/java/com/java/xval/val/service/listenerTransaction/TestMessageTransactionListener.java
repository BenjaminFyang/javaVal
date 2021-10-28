package com.java.xval.val.service.listenerTransaction;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java.xval.val.mapper.TransactionLogMapper;
import com.java.xval.val.model.Order;
import com.java.xval.val.model.TransactionLog;
import com.java.xval.val.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;


@Slf4j
@RocketMQTransactionListener
public class TestMessageTransactionListener implements RocketMQLocalTransactionListener {
    private final OrderService orderService;
    private final TransactionLogMapper rocketmqTransactionLogMapper;

    public TestMessageTransactionListener(OrderService orderService, TransactionLogMapper rocketmqTransactionLogMapper) {
        this.orderService = orderService;
        this.rocketmqTransactionLogMapper = rocketmqTransactionLogMapper;
    }

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        MessageHeaders headers = message.getHeaders();
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        Order order = (Order) o;

        try {
            //执行本地事务
            orderService.create(order, transactionId);
            //返回本地事务执行状态为提交，发送事务消息
            log.info("本地事务正常，消息可以被发送了..");
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            e.printStackTrace();
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        String transactionId = (String) message.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
        QueryWrapper<TransactionLog> objectQueryWrapper = new QueryWrapper<>();
        TransactionLog transactionLog = rocketmqTransactionLogMapper.selectOne(objectQueryWrapper.eq("id", transactionId));
        if (transactionLog != null) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }
}

