package com.myspringboot.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("order-timeout-demo")
public class OrderTimeoutListener {
    /** 订单业务服务，收到超时消息后检查并取消未支付订单。 */
    @Autowired
    private OrderTimeoutService orderService;

    /**
     * 消费者通过 @RabbitListener 订阅取消队列 CANCEL_QUEUE
     * RabbitMQ 主动将队列中的消息推送给消费者
     * 收到后调用 OrderTimeoutListener#onTimeout() 方法处理。
     * 消费者先订阅队列，RabbitMQ 再主动将消息推送给消费者
     *
     * RabbitMQ：消费者先订阅队列，然后由 RabbitMQ 主动推送消息给消费者，再由消费者消费消息
     * RabbitMQ：消费先者订阅队列 → RabbitMQ 推送消息 → 消费者处理。
     */
    // 只监听取消队列。等待队列必须让消息停留到 TTL 到期。
    @RabbitListener(queues = OrderTimeoutRabbitConfig.CANCEL_QUEUE)
    public void onTimeout(String orderId) {
        orderService.cancelIfUnpaid(orderId);
        // 默认 AUTO 模式：方法正常返回后由容器确认；异常应抛出，不要吞掉。
    }
}
