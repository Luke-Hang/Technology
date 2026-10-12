# 下单 30 分钟未支付，自动取消订单

这里的业务动作是取消未支付订单。真实支付平台的关单接口、退款不在这个学习示例中。

## 核心流程

```text
下单，保存 UNPAID 订单
发送订单 ID 到 WAIT_QUEUE RabbitMQ 的等待队列
    ↓
消息等待 30 分钟，到期
    ↓
RabbitMQ 通过死信交换机把消息转发到 CANCEL_QUEUE
    ↓
Spring 的监听容器从 CANCEL_QUEUE 收到消息
  @RabbitListener(queues = OrderTimeoutRabbitConfig.CANCEL_QUEUE)
  public void onTimeout(String orderId) {
        orderService.cancelIfUnpaid(orderId);
    }
    ↓
自动调用 onTimeout(orderId)
    ↓
执行 cancelIfUnpaid(orderId)
    ↓
仍未支付且已到期 → 取消订单
```

使用固定的队列级消息 TTL：`x-message-ttl = 1800000`，单位毫秒。不需要安装延迟插件。
注意 `x-expires` 是队列过期，与本方案不同。到期后消费可能稍有延迟，不保证精确到秒。

## 文件阅读顺序

1. `OrderTimeoutRabbitConfig.java`：等待队列、死信交换机、取消队列及绑定。
2. `OrderTimeoutService.java`：下单发消息、模拟支付、取消及状态判断。
3. `OrderTimeoutListener.java`：接收过期订单 ID。
4. `OrderTimeoutController.java`：下单、查询、模拟支付接口。
5. `Order.java`：独立的订单实体，保存订单 ID、状态和到期时间。

## 初次使用 RabbitMQ：先认识几个概念

RabbitMQ 是独立运行的消息服务。Java 程序把消息发给它，消费者再接收消息并执行业务。本例的消息内容只有订单 ID，订单状态保存在 Java 的 Map 中。

| 概念 | 在本例中的作用 |
| --- | --- |
| 生产者 | `OrderTimeoutService`，下单后发送订单 ID |
| 交换机（Exchange） | 按路由规则把消息送到队列 |
| 队列（Queue） | 存放消息，等待消费或过期 |
| 路由键（Routing Key） | 交换机用来决定把消息送到哪个队列的标识 |
| 消费者 | `OrderTimeoutListener`，接收订单 ID 并调用取消逻辑 |
| 消息 TTL | 消息在队列里的有效时长，本例默认 30 分钟 |
| 死信 | 本例中因 TTL 到期而过期的消息；过期是消息成为死信的一种原因 |

## 按代码理解完整流程

### 1. 下单并保存订单

用户调用 `POST /demo/orders`，`OrderTimeoutController.create()` 调用 `OrderTimeoutService.createOrder()`，创建订单：

```java
Order order = new Order(UUID.randomUUID().toString(), "UNPAID",
        System.currentTimeMillis() + timeoutMs);
saveOrders(order);
```

`saveOrders()` 内部用 `orders.put(order.getId(), order)` 模拟保存到数据库。

| 字段 | 含义 |
| --- | --- |
| `id` | 订单唯一编号，也是消息内容 |
| `status` | UNPAID 未支付、PAID 已支付、CANCELLED 已取消 |
| `timeoutMs` | 允许支付的时长，默认 1800000 毫秒，即 30 分钟 |
| `expireAt` | 订单支付截止时间，等于下单时的当前毫秒时间戳加上 `timeoutMs` |

例如 10:00 下单，允许支付 30 分钟，`expireAt` 就是当天 10:30 对应的毫秒时间戳。

### 2. 发送订单 ID 到等待队列

保存订单后，Service 执行：

```java
rabbitTemplate.convertAndSend("", OrderTimeoutRabbitConfig.WAIT_QUEUE, order.getId());
```

三个参数依次是：交换机名称、路由键、消息内容。交换机名称 `""` 表示 RabbitMQ 自带的默认交换机，它会将消息路由到名称与路由键相同的队列。因此这里把订单 ID 发到了 `WAIT_QUEUE`，即 `demo.order.wait.queue`。

只发送 ID，是为了后续按 ID 查询订单最新状态。如果用户在等待期间支付成功，消费者需要看到的是更新后的 PAID 状态。

### 3. RabbitMQ 保存消息并处理过期

对应配置在 `OrderTimeoutRabbitConfig.orderWaitQueue()`：

```java
args.put("x-message-ttl", timeoutMs);
args.put("x-dead-letter-exchange", CANCEL_EXCHANGE);
args.put("x-dead-letter-routing-key", CANCEL_KEY);
```

启用本示例 profile 后，Spring 在应用启动并连接 RabbitMQ 时通过 RabbitAdmin 声明这些队列、交换机和绑定。RabbitMQ 根据收到的配置处理后续消息：

1. 把订单 ID 消息存入等待队列。
2. 根据消息入队时间和消息 TTL(timeoutMs 消息过期时间) 判断是否过期。
3. 过期后，按配置把消息转发到死信交换机。

等待队列没有消费者，消息不会被立即取走。Java 发送后继续处理其他请求，不用为订单创建一个等待 30 分钟的线程。

**保存消息、判断过期、转发死信由 RabbitMQ 服务内部实现。你在项目里看到的是配置规则，不会找到一个自己编写的“等 30 分钟后转发”的 Java 方法。**

### 4. 死信交换机把消息路由到取消队列

对应绑定在 `OrderTimeoutRabbitConfig.orderCancelBinding()`：

```java
return BindingBuilder.bind(orderCancelQueue())
        .to(orderCancelExchange())
        .with(CANCEL_KEY);
```

这条规则表示：取消交换机收到路由键为 `order.cancel` 的消息时，把它送到取消队列。

```text
WAIT_QUEUE：demo.order.wait.queue
    ↓ 消息 TTL 到期
CANCEL_EXCHANGE：demo.order.cancel.exchange
    ↓ 按 CANCEL_KEY：order.cancel 路由
CANCEL_QUEUE：demo.order.cancel.queue
```

### 5. 监听容器收到取消队列的消息，调用 Java 方法

对应代码在 `OrderTimeoutListener`：

```java
@RabbitListener(queues = OrderTimeoutRabbitConfig.CANCEL_QUEUE)
public void onTimeout(String orderId) {
    orderService.cancelIfUnpaid(orderId);
}
```

`@RabbitListener` 让 Spring 的监听容器持续监听 `CANCEL_QUEUE`。消息进入该队列并被监听容器收到后，Spring 自动调用 `onTimeout()`，把消息内容传入 `orderId`，然后执行 `cancelIfUnpaid(orderId)`。

**监听方法不是等 30 分钟才开始监听，也不是反复执行取消代码。它在监听运行期间等待消息；每次收到消息，才执行这次处理。等待 30 分钟的效果来自上游等待队列的 TTL。**

因此，直接往 `CANCEL_QUEUE` 发消息，也会触发这个方法，不会再等待 30 分钟。如果消费者没启动，消息留在队列中，等消费者启动并收到消息后再处理；有消息积压时也可能延后处理。

本例采用默认 AUTO 确认模式：方法正常返回后，容器向 RabbitMQ 确认消费完成。这里的 AUTO 是 Spring 容器管理确认，不表示 RabbitMQ 在业务处理前就确认。处理异常应抛出，后续重试或重新入队行为取决于监听容器配置。

### 6. 检查最新状态，只取消超时未支付订单

对应代码在 `OrderTimeoutService.cancelIfUnpaid()`，核心判断是：

```java
if ("UNPAID".equals(order.getStatus())
        && System.currentTimeMillis() >= order.getExpireAt()) {
    return new Order(id, "CANCELLED", order.getExpireAt());
}
```

RabbitMQ 传来的消息是一次检查提醒，真正修改订单状态的是 Java 业务代码。只有仍未支付且已经到期才取消；已支付、已取消直接跳过。

| 时间 | 一直未支付 | 提前支付 |
| --- | --- | --- |
| 10:00 | 保存 UNPAID 订单并发送消息 | 保存 UNPAID 订单并发送消息 |
| 10:10 | 无操作 | 支付接口把订单改为 PAID |
| 10:30 左右 | 消息被消费，订单改为 CANCELLED | 消息被消费，发现 PAID，跳过 |

支付成功后不用删除等待队列中的消息，让它正常到期即可。重复收到同一订单的消息时，已取消的订单也会跳过，这就是取消操作的幂等处理。

本例用 `ConcurrentHashMap.computeIfPresent` 对同一订单原子更新，支付和取消都经过此操作，避免并发覆盖状态。实际项目用数据库条件更新实现。

### 7. 为什么既要 TTL，又要 expireAt？

TTL 负责让检查消息延迟约 30 分钟到达；`expireAt` 负责判断订单在业务上是否超过支付截止时间。消息从入队开始计算 TTL，订单截止时间在创建订单时计算，两者不是同一个计时起点。

MQ 投递和消费可能延迟。例如 10:30 到期，消费者 10:30:02 才执行取消。支付接口也检查 `expireAt`，因此在取消消息尚未处理时，超过截止时间的订单也不能再模拟支付成功。

## 如何演示

项目已包含 `spring-boot-starter-amqp`，无需新增依赖。启动本地 RabbitMQ，并在项目原有可运行配置的基础上，给 `TechnologyApplication` 添加启动参数：

```text
--spring.profiles.active=order-timeout-demo
--spring.rabbitmq.host=localhost
--spring.rabbitmq.port=5672
--spring.rabbitmq.username=guest
--spring.rabbitmq.password=guest
```

如原来启用了其他 profile，用逗号把 `order-timeout-demo` 追加进去。此项目还有数据库、安全配置以及旧 RabbitMQ 监听器，仍需满足它们的原有启动条件；本例不会修改这些配置。以下请求如受原有安全配置保护，需带已有登录凭证。

为了快速观察，首次演示可再加 `--demo.order.timeout-ms=10000`，把 30 分钟改成 10 秒；订单截止时间和队列 TTL 使用同一个配置。
**已声明的队列不能直接修改 TTL 参数**：切换 10 秒 / 30 分钟前，停止应用，仅删除 RabbitMQ 中本示例的 `demo.order.wait.queue`，再重启使其重新声明（队列中旧消息会丢失）。


## 面试怎么说

使用消息队列 RabbitMQ
1.用户下单后，先保存待支付订单到数据库，再把订单 ID 发到 RabbitMQ 等待队列。
2.等待队列设置 30 分钟的消息 TTL，不配置消费者。消息等待 30 分钟过期后，RabbitMQ 通过死信交换机将其转发到 CANCEL_QUEUE 取消队列
3.Spring 的监听容器监听到 CANCEL_QUEUE 取消队列中的消息后，自动调用 onTimeout()，传入订单 ID。
4.将仍未支付且已超过支付截止时间的订单改为已取消状态

```sql
UPDATE orders
SET status = 'CANCELLED'
WHERE id = ? AND status = 'UNPAID' AND expire_at <= CURRENT_TIMESTAMP;
```

影响行数为 1 才代表本次取消成功。不要先查状态，再无条件更新；本例以 `ConcurrentHashMap.computeIfPresent` 模拟单机原子更新。

学习版省略了生产可靠性配套：订单提交与发消息的一致性可用本地消息表及重试解决；Publisher Confirm / Return 检查消息是否成功到达并路由；定时扫描超时未支付订单作为补偿。当前发送没有实现这些机制，发送没有抛异常也不代表可靠投递成功。持久化队列也不代表内存订单能持久化。实际支付回调与关单、迟到付款的退款处理需结合支付平台设计。
