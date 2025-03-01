package com.proxy.dynamicProxy;

/**
 * @author xiehang
 * @date 2025/1/23 21:30
 * 目标对象
 */
public class SmsServiceImpl2 implements SmsService{
    public String send(String message) {
        System.out.println("send message:" + message);
        return message;
    }
}
