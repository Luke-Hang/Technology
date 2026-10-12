package com.myspringboot.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo/orders")
@Profile("order-timeout-demo")
public class OrderTimeoutController {
    /** 订单业务服务，负责下单、查询和模拟支付。 */
    @Autowired
    private OrderTimeoutService orderService;

    /**
     * 用户下单
     * @return
     */
    @PostMapping
    public Order create() {
        return orderService.createOrder();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable("id") String id) {
        Order order = orderService.getOrder(id);
        return order == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(order);
    }

    /** 仅模拟支付成功的状态更新，不会调用真实支付平台。 */
    @PostMapping("/{id}/pay")
    public ResponseEntity<Order> pay(@PathVariable("id") String id) {
        Order order = orderService.pay(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return "PAID".equals(order.getStatus()) ? ResponseEntity.ok(order)
                : ResponseEntity.status(HttpStatus.CONFLICT).body(order);
    }
}
