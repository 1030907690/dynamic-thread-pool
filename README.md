# dynamic-thread-pool
- 动态线程池

- 使用 nacos 2.1.0

- 配置文件内容
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