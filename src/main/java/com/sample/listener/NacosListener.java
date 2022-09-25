package com.sample.listener;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.sample.properties.DynamicThreadPoolProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author Zhou Zhongqing
 * @ClassName NacosListener
 * @description: 监听Nacos配置变化
 * @date 2022-09-25 15:09
 */
@Component
public class NacosListener {

    @Resource
    private ApplicationContext applicationContext;


    @NacosConfigListener(dataId = "dynamic-thread-pool.yaml", converter = DynamicThreadPoolProperties.class)
    public void onReceived(DynamicThreadPoolProperties properties) {
        System.out.println("onReceived " + properties);

        // 循环设置线程池的核心线程数和最大线程数
        List<DynamicThreadPoolProperties.ExecutorProperties> executorProperties = properties.getExecutors();
        for (DynamicThreadPoolProperties.ExecutorProperties executorProperty : executorProperties) {
            ThreadPoolTaskExecutor threadPoolTaskExecutor = applicationContext.getBean(executorProperty.getName(), ThreadPoolTaskExecutor.class);

            System.out.println(executorProperty.getName() + " 重新配置 核心线程数: " + executorProperty.getCorePoolSize() + " 最大线程数 ：" + executorProperty.getMaximumPoolSize());
            threadPoolTaskExecutor.setCorePoolSize(executorProperty.getCorePoolSize());
            threadPoolTaskExecutor.setMaxPoolSize(executorProperty.getMaximumPoolSize());
        }
    }


}
