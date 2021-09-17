package com.java.xval.val.common.thread;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * <消费队列线程>
 *
 * @author fangyang
 * @create 2020-09-02
 * @since 1.0.0
 */
@Configuration
public class TreadPoolConfig {

    // 核心线程数
    private static final int CORE_POOL_SIZE = 5;

    // 最大的线程数.
    private static final int MAX_POOL_SIZE = 20;

    // 最大的的队列的数量.
    private static final int QUEUE_CAPACITY = 300;

    // 线程生存时间.
    private static final Long KEEP_ALIVE_TIME = 3L;


    /**
     * 政策包导入初始化全局的线程池.
     * 初始化一个全局的线程池.
     *
     * @return the ExecutorService
     */
    @Bean(value = "consumerQueueThreadPool")
    public ExecutorService buildConsumerQueueThreadPool() {

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().
                setNameFormat("consumer-queue_one-thread-%d")
                .build();

        ExecutorService executorService = buildExecutor(namedThreadFactory);
        return TtlExecutors.getTtlExecutorService(executorService);
    }


    private static ExecutorService buildExecutor(ThreadFactory namedThreadFactory) {

        // 通过ThreadPoolExecutor构造函数自定义参数创建
        // 当任务添加到线程池中被拒绝时，会在线程池当前正在运行的Thread线程池中处理被拒绝的任务。
        return ExecutorBuilder.create().setCorePoolSize(CORE_POOL_SIZE)
                .setMaxPoolSize(MAX_POOL_SIZE)
                .setKeepAliveTime(KEEP_ALIVE_TIME, TimeUnit.MINUTES)
                .setWorkQueue(new ArrayBlockingQueue<>(QUEUE_CAPACITY))
                .setThreadFactory(namedThreadFactory)
                .setHandler(RejectPolicyEnum.ABORT.getValue())
                .build();

    }

    @Bean(value = "consumerQueueTwoThreadPool")
    public ExecutorService newExecutor() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().
                setNameFormat("consumer-queue_two-thread-%d")
                .build();

        ExecutorService executorService = buildExecutor(namedThreadFactory);
        return TtlExecutors.getTtlExecutorService(executorService);

    }

}
