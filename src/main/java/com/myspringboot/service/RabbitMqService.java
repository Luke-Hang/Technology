package com.myspringboot.service;

import com.myspringboot.vo.UserVo;

/**
 * @author xiehang
 * @create 2022-08-04 22:37
 */
public interface RabbitMqService {

    // 发送字符消息
    public void sendMsg(String msg);

    // 发送用户消息
    public void sendUser(UserVo user);
}
