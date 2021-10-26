package com.java.xval.val.service.listenerTransaction;

import com.alibaba.fastjson.JSONObject;
import com.java.xval.val.model.Order;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PointTransactionListener implements MessageListenerConcurrently {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext context) {
        logger.info("消费者线程监听到消息。");
        try {
            for (MessageExt message : list) {
                logger.info("开始处理订单数据，准备增加积分....");
                Order order = JSONObject.parseObject(message.getBody(), Order.class);
                if (!processor(message)) {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }

                // todo 开始插入对应的积分数据.
                logger.info("开始插入积分数据，增加积分....");
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error("处理消费者数据发生异常", e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }

    /**
     * 消息处理，第3次处理失败后，发送邮件或者短信通知人工介入
     *
     * @param message the message
     * @return boolean
     */
    private boolean processor(MessageExt message) {
        String body = new String(message.getBody());
        try {
            logger.info("PointTransactionListener消息处理....{}", body);
            int k = 1 / 0;
            return true;
        } catch (Exception e) {
            if (message.getReconsumeTimes() >= 3) {
                logger.error("PointTransactionListener消息重试已达最大次数，将通知业务人员排查问题。{}", message.getMsgId());
                // todo 发送短信或者邮件通知.
                logger.info("PointTransactionListener发送短信或者邮件通知");
                return true;
            }
            return false;
        }
    }
}

