package com.proxy.staticProxy;

import com.proxy.staticProxy.SmsProxy;
import com.proxy.staticProxy.SmsServiceImpl;

/**
 * @author xiehang
 * @date 2025/1/23 21:36
 *
 * 测试
 */
public class TestStaticProxy {
    public static void main(String[] args) {
        SmsServiceImpl smsServiceImpl = new SmsServiceImpl();
        //通过代理类屏蔽对目标对象的访问
        SmsProxy smsProxy = new SmsProxy(smsServiceImpl);
        smsProxy.proxySend("java");
    }
}
