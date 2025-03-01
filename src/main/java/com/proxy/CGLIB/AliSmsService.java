package com.proxy.CGLIB;

/**
 * @author xiehang
 * @date 2025/1/24 14:37
 *
 * 使用阿里云发送短信的类
 */
public class AliSmsService {
    public String send(String message) {
        System.out.println("send message:" + message);
        return message;
    }
}
