package com.sample.threadpool;

import com.sample.properties.DynamicThreadPoolProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author Zhou Zhongqing
 * @ClassName RegisterThreadPool
 * @description: 注册线程池
 * @date 2022-09-25 15:09
 */
@Component
public class RegisterThreadPool implements BeanDefinitionRegistryPostProcessor , EnvironmentAware {


    private Environment environment;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {

        // 读取配置
        DynamicThreadPoolProperties dynamicThreadPoolProperties = new DynamicThreadPoolProperties();
        Binder binder = Binder.get(environment);
        ResolvableType resolvableType = ResolvableType.forClass(DynamicThreadPoolProperties.class);
        Bindable<Object> objectBindable = Bindable.of(resolvableType).withExistingValue(dynamicThreadPoolProperties);
        binder.bind("dynamic-thread-pool", objectBindable);

        List<DynamicThreadPoolProperties.ExecutorProperties> executorProperties = dynamicThreadPoolProperties.getExecutors();
        for (DynamicThreadPoolProperties.ExecutorProperties executorProperty : executorProperties) {
            GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
            genericBeanDefinition.setBeanClass(DynamicThreadPoolExecutor.class);
            // 核心线程数
            genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(executorProperty.getCorePoolSize());
            // 最大线程数
            genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(executorProperty.getMaximumPoolSize());
            // 线程名称前缀
            genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(executorProperty.getName());
            // 注册Bean
            beanDefinitionRegistry.registerBeanDefinition(executorProperty.getName(), genericBeanDefinition);
            System.out.println("注册 " + executorProperty.getName() + " 线程池" );
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
     this.environment = environment;
    }
}
