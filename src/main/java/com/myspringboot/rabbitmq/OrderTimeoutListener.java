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
     * Spring 为 @RabbitListener 创建的后台线程持续从取消队列 CANCEL_QUEUE 拉取消息
     * 收到后调用 OrderTimeoutListener#onTimeout() 方法处理。
     */
    @RabbitListener(queues = OrderTimeoutRabbitConfig.CANCEL_QUEUE)
    public void onTimeout(String orderId) {
        orderService.cancelIfUnpaid(orderId);
        // 默认 AUTO 模式：方法正常返回后由容器确认；异常应抛出，不要吞掉。
    }
}
