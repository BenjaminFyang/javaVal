package com.java.xval.val.service.listenerTransaction;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.java.xval.val.model.Order;
import com.java.xval.val.service.OrderService;
import com.java.xval.val.service.TransactionLogService;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 订单分布式事务RocketMQ 生产者
 */
@Component
public class OrderTransactionListener implements TransactionListener {

    @Resource
    private OrderService orderService;

    @Resource
    private TransactionLogService transactionLogService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {

        //  本地事务执行会有三种可能
        //  1、commit 成功
        //  2、Rollback 失败
        //  3、网络等原因服务宕机收不到返回结果
        //  执行创建订单的本地事务，这里完成订单数据和事务日志的插入.
        logger.info("OrderTransactionListener开始执行本地事务message={}....", JSON.toJSONString(message));
        LocalTransactionState state;
        try {
            String body = new String(message.getBody());
            Order order = JSONObject.parseObject(body, Order.class);
            orderService.create(order, message.getTransactionId());
            state = LocalTransactionState.COMMIT_MESSAGE;
            logger.info("OrderTransactionListener本地事务已提交。{}", message.getTransactionId());
        } catch (Exception e) {
            logger.info("OrderTransactionListener执行本地事务失败", e);
            state = LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return state;
    }

    /**
     * 只有上面接口返回 LocalTransactionState.UNKNOW 才会调用查接口被调用
     *
     * @param messageExt the messageExt
     * @return LocalTransactionState 事务状态.
     * @see org.apache.rocketmq.client.producer.LocalTransactionState
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {

        // 因为有种情况就是：上面本地事务执行成功了，但是return LocalTransactionState.COMMIT_MESSAG的时候服务挂了，那么最终 Brock还未收到消息的二次确定，还是个预消息，所以当重新启动的时候还是回调这个回调接口。
        // 如果不先查询上面本地事务的执行情况 直接在执行本地事务，那么就相当于成功执行了两次本地事务了。
        logger.info("OrderTransactionListener开始回查本地事务状态{}", messageExt.getTransactionId());
        LocalTransactionState state;
        String transactionId = messageExt.getTransactionId();

        if (StringUtils.isNotBlank(transactionLogService.get(transactionId))) {
            state = LocalTransactionState.COMMIT_MESSAGE;
        } else {
            state = LocalTransactionState.UNKNOW;
        }
        logger.info("OrderTransactionListener结束本地事务状态查询：{}", state);
        return state;
    }
}
