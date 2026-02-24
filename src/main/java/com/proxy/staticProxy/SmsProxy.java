package com.proxy.staticProxy;

/**
 * @author xiehang
 * @date 2025/1/23 21:31
 *
 * 代理类
 */
public class SmsProxy {

    //使用构造方法注入，将需要代理的目标对象注入代理类
    private final SmsServiceImpl smsServiceImpl;

    public SmsProxy(SmsServiceImpl smsServiceImpl) {
        this.smsServiceImpl = smsServiceImpl;
    }

    /**
     * 在目标方法执行前后做一些自己想做的事情
     *
     * @param message
     * @return
     */
    public String proxySend(String message) {
        //调用方法之前，我们可以添加自己的操作
        System.out.println("before method send()");
        //在代理类的对应方法调用目标类中的对应方法
        smsServiceImpl.send(message);
        //调用方法之后，我们同样可以添加自己的操作
        System.out.println("after method send()");
        return null;
    }
}
