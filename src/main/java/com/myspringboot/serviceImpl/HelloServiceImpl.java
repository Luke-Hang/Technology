package com.myspringboot.serviceImpl;

import com.myspringboot.service.HelloService;

/**
 * @author xiehang
 * @create 2022-08-23 22:32
 * 服务提供者实现类
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public void sayHello(String name) {
        System.out.println("hello"+name);
    }
}
