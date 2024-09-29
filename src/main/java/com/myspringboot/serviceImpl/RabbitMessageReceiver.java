package com.myspringboot.serviceImpl;

import com.myspringboot.vo.UserVo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author xiehang
 * @create 2022-08-04 23:09
 */

@Component
public class RabbitMessageReceiver {

    // 定义监听字符串队列名称
    @RabbitListener(queues = {"${rabbitmq.queue.msg}"})
    public void receiveMsg(String msg){
        System.out.println("收到消息: 【" + msg + "】");
    }

    // 定义监听用户队列名称
    @RabbitListener(queues = {"${rabbitmq.queue.user}"})
    public void receiveUser(UserVo user) {
        System.out.println("收到用户信息【" + user + "】");
    }
}
