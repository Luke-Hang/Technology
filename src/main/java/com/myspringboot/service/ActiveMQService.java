package com.myspringboot.service;

/**
 * @author xiehang
 * @create 2022-08-02 8:21
 *
 * 定义一个接口，它既能够发送JMS消息，也能够接收JMS消息
 */

public interface ActiveMQService {

    // 发送消息
    public void sendMsg(String message);

    // 接收消息
    public void receiveMsg(String message);
}
