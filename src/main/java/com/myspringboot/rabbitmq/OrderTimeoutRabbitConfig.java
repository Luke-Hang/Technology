package com.myspringboot.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

/** 消息先等待 30 分钟，再通过死信交换机转到取消队列。 */
@Configuration
@Profile("order-timeout-demo")
public class OrderTimeoutRabbitConfig {
    /** 等待队列名称：存放待过期的订单消息，不配置消费者。 */
    public static final String WAIT_QUEUE = "demo.order.wait.queue";

    /** 死信交换机名称：接收等待队列中过期的消息。 */
    public static final String CANCEL_EXCHANGE = "demo.order.cancel.exchange";

    /** 取消队列名称：消费者监听此队列，检查并取消超时未支付订单。 */
    public static final String CANCEL_QUEUE = "demo.order.cancel.queue";

    /** 死信路由键：与绑定的路由键一致，将过期消息路由到取消队列。 */
    public static final String CANCEL_KEY = "order.cancel";

    @Bean
    public Queue orderWaitQueue(@Value("${demo.order.timeout-ms:1800000}") int timeoutMs) {
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("订单超时时间必须大于 0");
        }
        Map<String, Object> args = new HashMap<>();
        // 消息在等待队列中的有效时长，默认 30 分钟
        args.put("x-message-ttl", timeoutMs);

        // 消息过期后，转发到哪个交换机
        //RabbitMQ 通过死信交换机把消息转发到 CANCEL_QUEUE
        args.put("x-dead-letter-exchange", CANCEL_EXCHANGE);

        // 转发时使用哪个路由键
        args.put("x-dead-letter-routing-key", CANCEL_KEY);
        //保存消息、判断消息过期、转发过期消息，这些操作由 RabbitMQ 服务内部完成，你不用自己写计时代码。
        return new Queue(WAIT_QUEUE, true, false, false, args);
    }

    @Bean
    public DirectExchange orderCancelExchange() {
        return new DirectExchange(CANCEL_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderCancelQueue() {
        return new Queue(CANCEL_QUEUE, true);
    }

    @Bean
    public Binding orderCancelBinding() {
        return BindingBuilder.bind(orderCancelQueue()).to(orderCancelExchange()).with(CANCEL_KEY);
    }
}
