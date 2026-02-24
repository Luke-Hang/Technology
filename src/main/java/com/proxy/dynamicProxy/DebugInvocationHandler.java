package com.proxy.dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author xiehang
 * @date 2025/1/23 21:51
 *
 * JDK 动态代理类
 */
public class DebugInvocationHandler implements InvocationHandler {

    /**
     * 代理类中的真实对象
     */
    private final Object target;

    public DebugInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //调用方法之前，我们可以添加自己的操作
        System.out.println("before method " + method.getName());
        //invoke() 方法: 当我们的动态代理对象调用原生方法的时候，最终实际上调用到的是 invoke() 方法，
        // 然后 invoke() 方法代替我们去调用了被代理对象的原生方法
        Object result = method.invoke(target, args);
        //调用方法之后，我们同样可以添加自己的操作
        System.out.println("after method " + method.getName());
        return result;
    }
}
