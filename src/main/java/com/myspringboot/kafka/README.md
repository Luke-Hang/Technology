# 发送删除主机消息

调用 `POST /kafka/hosts/delete`，JSON 请求体：

```json
{"hostId":"vm-001"}
```

向 `host-delete` Topic 发送 `{"operation":"DELETE_HOST","hostId":"vm-001"}`，key 为主机 ID。Broker 确认后返回 `SENT`，发送失败则接口报错。

Kafka 地址读取 `spring.kafka.bootstrap-servers`；Topic 读取 `spring.kafka.template.default-topic`。
