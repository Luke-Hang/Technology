package com.myspringboot.serviceImpl;

import com.myspringboot.service.ActiveMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * @author xiehang
 * @create 2022-08-02 8:23
 */


/**
 * ActiveMQ服务接口
 */
@Service
public class ActiveMQServiceImpl implements ActiveMQService {

    // 注入由Spring Boot自动生产的jmsTemplate
    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public void sendMsg(String message) {
        System.out.println("发送消息【" + message + "】");
        jmsTemplate.convertAndSend(message);
        //自定义发送地址
        //jmsTemplate.convertAndSend("your-destination", message);
    }

    @Override
    @JmsListener(destination = "${spring.jms.template.default-destination}")
    // 使用注解，监听地址发送过来的消息
    public void receiveMsg(String message) {
        System.out.println("接收到消息：【" + message + "】");
    }
}
