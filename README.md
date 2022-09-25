# dynamic-thread-pool
- 动态线程池
## 使用技术
- Spring Boot 2.7.3
- 使用 Nacos 2.1.0

## 配置内容
- 配置文件内容 `dynamic-thread-pool.yaml`
```yaml
dynamic-thread-pool:
  executors:
    - name: orderThreadPool
      core-pool-size: 5
      maximum-pool-size: 10
    - name: smsThreadPool
      core-pool-size: 5
      maximum-pool-size: 10
```