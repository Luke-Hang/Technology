package com.myspringboot.service;

/**
 * @author xiehang
 * @create 2022-08-23 22:31
 *
 * 服务提供者接口，JDK动态代理接口是必须的
 */
public interface HelloService {
    public void sayHello(String name);
}
