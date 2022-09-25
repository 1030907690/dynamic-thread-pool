package com.sample.threadpool;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Zhou Zhongqing
 * @ClassName DynamicThreadPoolProperties
 * @description: 封装线程池
 * @date 2022-09-25 15:09
 */
public class DynamicThreadPoolExecutor extends ThreadPoolTaskExecutor {


    // 队列最大长度
    private int queueCapacity = 1000;

    // 线程池维护线程所允许的空闲时间
    private int keepAliveSeconds = 300;

    public DynamicThreadPoolExecutor(int corePoolSize, int maxPoolSize, String threadNamePrefix) {
        this.setMaxPoolSize(maxPoolSize);
        this.setCorePoolSize(corePoolSize);
        this.setQueueCapacity(queueCapacity);
        this.setKeepAliveSeconds(keepAliveSeconds);
        // 线程池对拒绝任务(无线程可用)的处理策略
        this.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //线程名称前缀
        this.setThreadNamePrefix(threadNamePrefix + "-");
    }
}
