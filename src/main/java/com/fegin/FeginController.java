package com.fegin;

import com.myspringboot.springcloud.model.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xiehang
 * @date 2025/1/7 21:46
 *
 * 将该接口注入到controller里面，直接调用对应的访问路径，即可完成负载均衡
 */
@RestController
public class FeginController {

    @Autowired
    private FeginService feginService;

    @RequestMapping(value = "/consumer/dept/get/{id}")
    public Dept get(@PathVariable("id") Long id) {
        return feginService.get(id);
    }

    @RequestMapping(value = "/consumer/dept/list")
    public List<Dept> list() {
        //先调用controller层的list(),再调用feginService.list()
        //该方法会通过fegin自动调用MICROSERVICECLOUD-DEPT服务的/dept/list方法
        return feginService.list();
    }

    @RequestMapping(value = "/consumer/dept/add")
    public Object add(Dept dept) {
        return feginService.add(dept);
    }
}
