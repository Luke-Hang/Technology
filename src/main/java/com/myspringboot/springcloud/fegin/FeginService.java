package com.myspringboot.springcloud.fegin;

import com.myspringboot.springcloud.model.Dept;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author xiehang
 * @date 2025/1/7 21:45
 *
 * 定义一个interface FeginService，添加@FeignClient 注解，
 * 指定微服务名称MICROSERVICECLOUD-DEPT
 * 请求地址@RequestMapping
 */
@FeignClient(value = "MICROSERVICECLOUD-DEPT")
@Component
public interface FeginService {
    @RequestMapping(value = "/dept/get/{id}", method = RequestMethod.GET)
    public Dept get(@PathVariable("id") long id);

    @RequestMapping(value = "/dept/list", method = RequestMethod.GET)
    public List<Dept> list();

    @RequestMapping(value = "/dept/add", method = RequestMethod.POST)
    public boolean add(Dept dept);
}
