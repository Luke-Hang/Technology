package com.myspringboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.myspringboot.service.RabbitMqService;
import com.myspringboot.vo.UserVo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiehang
 * @create 2022-08-04 23:17
 */
@RestController
@RequestMapping("/rabbitmq")
public class RabbitMqController {

    /**
     * 注入Spring Boot自定生成的对象
     */
    @Autowired
    private RabbitMqService rabbitMqService;

    /**
     * 使用RabbitMq发送字符串
     *
     * @param message
     * @return
     */
    @GetMapping("/msg")
    public Map<String, Object> msg(String message) {
        rabbitMqService.sendMsg(message);
        return resultMap("message", message);
    }

    /**
     * 使用RabbitMq发送POJO
     * @param id
     * @param userName
     * @param note
     * @return
     */
    @GetMapping("/user") // 用户
    public Map<String, Object> user(Long id, String userName, String note) {
        UserVo user = new UserVo(id, userName, note);
        rabbitMqService.sendUser(user);
        return resultMap("user", user);
    }

    // 结果Map
    private Map<String, Object> resultMap(String key, Object obj) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put(key, obj);
        return result;
    }
}

