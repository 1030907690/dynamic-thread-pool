package com.sample.properties;

import com.alibaba.nacos.api.config.convert.NacosConfigConverter;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.ByteArrayResource;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;


/**
 * @author Zhou Zhongqing
 * @ClassName DynamicThreadPoolProperties
 * @description: 配置内容的对象
 * @date 2022-09-25 15:09
 */
public class DynamicThreadPoolProperties implements NacosConfigConverter {

    private List<ExecutorProperties> executors;


    @Override
    public String toString() {
        return "DynamicThreadPoolProperties{" +
                "executors=" + executors +
                '}';
    }

    public List<ExecutorProperties> getExecutors() {
        return executors;
    }

    public void setExecutors(List<ExecutorProperties> executors) {
        this.executors = executors;
    }

    @Override
    public boolean canConvert(Class targetType) {
        return true;
    }

    // 转换字符串配置为对象
    @Override
    public Object convert(String config) {
        // 读取配置并转换成对象

        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ByteArrayResource(config.getBytes(StandardCharsets.UTF_8)));
        Properties properties = yaml.getObject();

        DynamicThreadPoolProperties dynamicThreadPoolProperties = new DynamicThreadPoolProperties();
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(source);
        ResolvableType resolvableType = ResolvableType.forClass(DynamicThreadPoolProperties.class);
        Bindable<Object> objectBindable = Bindable.of(resolvableType).withExistingValue(dynamicThreadPoolProperties);
        binder.bind("dynamic-thread-pool", objectBindable);
        return dynamicThreadPoolProperties;
    }

    public static class ExecutorProperties {


        private String name;

        private Integer corePoolSize = 5;

        private Integer maximumPoolSize = 10;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(Integer corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public Integer getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(Integer maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }

        @Override
        public String toString() {
            return "ExecutorProperties{" +
                    "name='" + name + '\'' +
                    ", corePoolSize=" + corePoolSize +
                    ", maximumPoolSize=" + maximumPoolSize +
                    '}';
        }
    }
}
