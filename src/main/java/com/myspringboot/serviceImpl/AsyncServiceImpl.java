package com.myspringboot.serviceImpl;

import com.myspringboot.service.AsyncService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author xiehang
 * @create 2022-08-06 9:29
 */
@Service
public class AsyncServiceImpl implements AsyncService {

    @Override
    // 声明使用异步调用
    @Async
    public void generateReport() {
        System.out.println("报表线程(异步方法线程)名称："
                + "【" +Thread.currentThread().getName()+"】");
    }
}
