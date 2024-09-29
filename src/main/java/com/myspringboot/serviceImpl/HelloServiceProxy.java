package com.myspringboot.serviceImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author xiehang
 * @create 2022-08-23 22:35
 * 动态代理类，提供真实对象的绑定和代理方法，动态代理类要求实现InvocationHandler接口的代理方法
 * 当一个对象被绑定后，执行其方法的时候就会进入到代理方法里面
 */
public class HelloServiceProxy implements InvocationHandler {

    /**
     * 真实服务对象
     */
    private Object target;


    public Object bind(){
        this.target=target;
        //取得代理对象
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(), this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("########我是JDK动态代理#########");
        Object result=null;
        //反射方法前调用
        System.err.println("我准备说hello。");
        //执行方法相当于调用HelloServiceImpl类的sayHello 方法
        method.invoke(target,args);
        //反射后调用方法
        System.err.println("我说过hello了。");
        return result;
    }
}
