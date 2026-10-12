package com.myspringboot.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** 学习用：以单机内存代替数据库，重启后订单会丢失。 */
@Service
@Profile("order-timeout-demo")
public class OrderTimeoutService {
    /** Spring 提供的 RabbitMQ 操作模板，用于发送订单 ID。 */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /** 允许支付的时长，单位毫秒；默认 1800000 毫秒（30 分钟），与等待队列的消息 TTL 使用同一配置。 */
    private final long timeoutMs;

    /** 模拟订单数据库：key 为订单 ID，value 为订单快照；仅存于当前进程，重启后丢失。 */
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public OrderTimeoutService(@Value("${demo.order.timeout-ms:1800000}") long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    /**
     *创建订单，状态为 UNPAID，同时记录到期时间
     * @return
     */
    public Order createOrder() {
        // expireAt 是具体的截止时间，timeoutMs 是时长。例如 10:00 下单，30 分钟后即 10:30 到期。
        //System.currentTimeMillis() 表示当前时间，表示超时时间默认 30 min
        Order order = new Order(UUID.randomUUID().toString(), "UNPAID",
                System.currentTimeMillis() + timeoutMs);

        //保存订单：未支付，截止时间为 30 分钟后
        saveOrders(order);
        try {
            // 把订单 ID 发给 RabbitMQ 的等待队列
            // 默认交换机按队列名路由；只发送订单 ID，不发送整份订单。
            rabbitTemplate.convertAndSend("", OrderTimeoutRabbitConfig.WAIT_QUEUE, order.getId());
        } catch (RuntimeException e) {
            orders.remove(order.getId());
            throw e;
        }
        return order;
    }

    /** 模拟数据库保存订单，实际项目中可替换为 Mapper 或 Repository 的保存操作。 */
    private void saveOrders(Order order) {
        orders.put(order.getId(), order);
    }

    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }

    public Order pay(String orderId) {
        // computeIfPresent 对同一订单的状态修改是原子的，支付与取消不能同时成功。
        return orders.computeIfPresent(orderId, (id, order) -> {
            if (!"UNPAID".equals(order.getStatus())) {
                return order;
            }
            // 即使 MQ 消费稍有延迟，到期后也不能再模拟支付成功。
            String status = System.currentTimeMillis() >= order.getExpireAt() ? "CANCELLED" : "PAID";
            return new Order(id, status, order.getExpireAt());
        });
    }

    public void cancelIfUnpaid(String orderId) {
        orders.computeIfPresent(orderId, (id, order) -> {
            if ("UNPAID".equals(order.getStatus())
                    && System.currentTimeMillis() >= order.getExpireAt()) {
                System.out.println("订单超时未支付，自动取消：" + id);
                return new Order(id, "CANCELLED", order.getExpireAt());
            }
            // 已支付、已取消直接跳过：重复消费不会重复取消。
            return order;
        });
    }
}
