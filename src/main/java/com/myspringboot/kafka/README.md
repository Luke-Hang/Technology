# 发送删除主机消息

调用 `POST /kafka/hosts/delete`，JSON 请求体：

```json
{"hostId":"vm-001"}
```

向 `host-delete` Topic 发送 `{"operation":"DELETE_HOST","hostId":"vm-001"}`，key 为主机 ID。Broker 确认后返回发送成功响应，发送失败则接口报错。

Kafka 地址读取 `spring.kafka.bootstrap-servers`；Topic 读取自定义配置 `spring.kafka.delete-topic`。

`KafkaController.consume` 使用 `@KafkaListener` 自动接收同一 Topic 的消息，消费者组沿用 `spring.kafka.consumer.group-id`。它解析 JSON、校验 operation 和 hostId，然后打印接收日志。实际删除主机的方法可在该方法末尾接入，当前不执行真实删除。

关闭消费者自动提交，使用 RECORD 模式由 Spring 容器在正常处理每条消息后提交 offset。消费失败会抛出异常交给容器处理；这里未额外配置重试或死信队列。Kafka 可能重复投递，接入真实删除时应保证业务幂等。
