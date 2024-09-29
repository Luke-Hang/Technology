package com.myspringboot.serviceImpl;

import com.myspringboot.service.RabbitMqService;
import com.myspringboot.vo.UserVo;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author xiehang
 * @create 2022-08-04 22:38
 */
@Service
// 实现ConfirmCallback接口，这样可以回调
public class RabbitMqServiceImpl implements RabbitMqService,RabbitTemplate.ConfirmCallback {

    @Value("${rabbitmq.queue.msg}")
    private String msgRouting=null;

    @Value("${rabbitmq.queue.user}")
    private String userRouting = null;

    // 注入由Spring Boot自动配置的RabbitTemplate
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 发送消息
    @Override
    public void sendMsg(String msg) {
        System.out.println("发送消息: 【" + msg + "】");
        // 发送消息，通过msgRouting确定队列---即给指定队列rabbitmq.queue.msg发送消息
        rabbitTemplate.convertAndSend(msgRouting, msg);
        // 设置了回调对象为当前对象,发送消息后，当消费者 得到消息时，它就会调用confirm方法
        rabbitTemplate.setConfirmCallback(this);
    }

    // 发送用户
    @Override
    public void sendUser(UserVo user) {
        System.out.println("发送用户消息: 【" + user + "】");
        //convertAndSend 转换和发送消息------即给指定队列rabbitmq.queue.user发送消息
        rabbitTemplate.convertAndSend(userRouting, user);
        rabbitTemplate.setConfirmCallback(this);
    }

    // 回调确认方法
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String cause) {
        if (b){
            System.out.println("消费消息成功");
        }else {
            System.out.println("消费消息失败:" + cause);
        }
    }
}
