package com.myspringboot.serviceImpl;

import com.myspringboot.service.ActiveMqUserService;
import com.myspringboot.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * @author xiehang
 * @create 2022-08-03 9:20
 */
@Service
public class ActiveMqUserServiceImpl implements ActiveMqUserService {

    // 注入由Spring Boot自动生产的jmsTemplate
    @Autowired
    private JmsTemplate jmsTemplate;

    // 自定义地址
    private static final String myDestination = "my-destination";

    @Override
    public void sendUser(UserVo user) {
        System.out.println("发送消息【" + user + "】");
        // 使用自定义地址发送对象
        jmsTemplate.convertAndSend(myDestination, user);
    }

    @Override
    // 监控自定义地址
    @JmsListener(destination = myDestination)
    public void receiveUser(UserVo user) {
        System.out.println("接收到消息：【" + user + "】");
    }
}
