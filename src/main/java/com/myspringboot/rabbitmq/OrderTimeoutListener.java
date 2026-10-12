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

    // 只监听取消队列。等待队列必须让消息停留到 TTL 到期。
    @RabbitListener(queues = OrderTimeoutRabbitConfig.CANCEL_QUEUE)
    public void onTimeout(String orderId) {
        orderService.cancelIfUnpaid(orderId);
        // 默认 AUTO 模式：方法正常返回后由容器确认；异常应抛出，不要吞掉。
    }
}
