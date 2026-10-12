package com.myspringboot.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 订单实体，保存订单编号、状态和支付截止时间。 */
@Getter
@AllArgsConstructor
public class Order {
    /** 订单唯一编号，下单时生成 UUID，也是发送到 RabbitMQ 的消息内容。 */
    private String id;
    /** 订单状态：UNPAID 未支付，PAID 已支付，CANCELLED 已取消。 */
    private String status;
    /** 支付截止时间：从 1970-01-01 00:00:00 UTC 起算的毫秒时间戳，等于下单时的当前时间 + timeoutMs。 */
    private long expireAt;

}
