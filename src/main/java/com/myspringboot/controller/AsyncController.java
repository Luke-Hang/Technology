package com.myspringboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.myspringboot.service.AsyncService;

/**
 * @author xiehang
 * @create 2022-08-06 9:28
 */
@RestController
@RequestMapping("/async")
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    @GetMapping("/page")
    public String asyncPage() {
        System.out.println("请求线程  (控制器线程)名称：" + "【" + Thread.currentThread().getName()+ "】");
        asyncService.generateReport();
        return "async";
    }
}
