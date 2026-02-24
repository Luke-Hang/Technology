package com.fegin;

import com.myspringboot.springcloud.model.Dept;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xiehang
 * @date 2025/1/7 21:45
 * 定义一个interface FeginService，添加@FeignClient 注解
 * 指定微服务名称MICROSERVICECLOUD-DEPT
 * 请求地址@RequestMapping
 */
@FeignClient(name = "example-client",value = "MICROSERVICECLOUD-DEPT")
/*//指定目标服务地址
@FeignClient(name = "example-client", value = "https://jsonplaceholder.typicode.com") */
public interface FeginService {
    @RequestMapping(value = "/dept/get/{id}", method = RequestMethod.GET)
    public Dept get(@PathVariable("id") long id);

    @GetMapping(value = "/dept/list")
    public List<Dept> list();

    @PostMapping(value = "/dept/add")
    public boolean add(Dept dept);
}
