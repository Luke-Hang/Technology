package com.proxy.staticProxy;

import com.proxy.dynamicProxy.SmsService;

/**
 * @author xiehang
 * @date 2025/1/23 21:30
 * 目标对象
 */
public class SmsServiceImpl implements SmsService {
    public String send(String message) {
        System.out.println("send message:" + message);
        return message;
    }
}
