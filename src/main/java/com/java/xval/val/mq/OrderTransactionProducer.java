package com.java.xval.val.mq;


import com.java.xval.val.service.listenerTransaction.OrderTransactionListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 订单事务监听器.
 */
@Component
public class OrderTransactionProducer extends TransactionProducer {

    // 用于执行本地事务和事务状态回查的监听器 需要自定义事务监听器 用于事务的二次确认和事务回查
    @Resource
    private OrderTransactionListener orderTransactionListener;

    // 官方建议自定义线程 给线程取自定义名称 发现问题更好排查
    private final ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200), r -> {
        Thread thread = new Thread(r);
        thread.setName("client-transaction-producer-check-thread");
        return thread;
    });

    // Spring容器启动的时候初始化订单事务监听器.
    @PostConstruct
    public void buildInit() {
        init(orderTransactionListener, executorService);
    }
}
