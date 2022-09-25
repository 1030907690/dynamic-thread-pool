package com.sample.listener;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.sample.properties.DynamicThreadPoolProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Zhou Zhongqing
 * @ClassName NacosListener
 * @description: 监听Nacos配置变化
 * @date 2022-09-25 15:09
 */
@Component
public class NacosListener implements Listener, InitializingBean {

    @NacosInjected
    private ConfigService configService;

    @Resource
    private Environment environment;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public Executor getExecutor() {
        return Executors.newFixedThreadPool(1);
    }

    @Override
    public void receiveConfigInfo(String config) {
        System.out.println(" receiveConfigInfo " + config);
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ByteArrayResource(config.getBytes(StandardCharsets.UTF_8)));
        Properties properties = yaml.getObject();

        // 读取配置
        DynamicThreadPoolProperties dynamicThreadPoolProperties = new DynamicThreadPoolProperties();
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
//        Binder binder = Binder.get(environment);
        Binder binder = new Binder(source);
        ResolvableType resolvableType = ResolvableType.forClass(DynamicThreadPoolProperties.class);
        Bindable<Object> objectBindable = Bindable.of(resolvableType).withExistingValue(dynamicThreadPoolProperties);
        binder.bind("dynamic-thread-pool", objectBindable);
        // 循环设置线程池的核心线程数和最大线程数
        List<DynamicThreadPoolProperties.ExecutorProperties> executorProperties = dynamicThreadPoolProperties.getExecutors();
        for (DynamicThreadPoolProperties.ExecutorProperties executorProperty : executorProperties) {
            ThreadPoolTaskExecutor threadPoolTaskExecutor = applicationContext.getBean(executorProperty.getName(), ThreadPoolTaskExecutor.class);

            System.out.println(executorProperty.getName() + " 重新配置 核心线程数: " + executorProperty.getCorePoolSize() + " 最大线程数 ：" + executorProperty.getMaximumPoolSize());
            threadPoolTaskExecutor.setCorePoolSize(executorProperty.getCorePoolSize());
            threadPoolTaskExecutor.setMaxPoolSize(executorProperty.getMaximumPoolSize());
        }

    }


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

    @Override
    public void afterPropertiesSet() throws Exception {
        // configService.addListener("dynamic-thread-pool.yaml", "DEFAULT_GROUP", this);
    }
}
