package com.proxy.dynamicProxy;

import com.proxy.staticProxy.SmsServiceImpl;

/**
 * @author xiehang
 * @date 2025/1/24 10:05
 */
public class TestdynamicProxy {
    public static void main(String[] args) {
        SmsService proxy = (SmsService) JdkProxyFactory.getProxy(new SmsServiceImpl());
        proxy.send("java");
    }
}
