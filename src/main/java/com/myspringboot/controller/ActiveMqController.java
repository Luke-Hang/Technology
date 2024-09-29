package com.myspringboot.controller;

import com.myspringboot.vo.UserVo;
import com.myspringboot.service.ActiveMQService;
import com.myspringboot.service.ActiveMqUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiehang
 * @create 2022-08-03 9:28
 */
@RestController
@RequestMapping("/activemq")
public class ActiveMqController {

    // 注入服务对象
    @Autowired
    private ActiveMQService activeMQService;

    @Autowired
    private ActiveMqUserService activeMqUserService;

    // 测试User对象的发送
    @GetMapping("/user")
    public Map<String, Object> sendUser(Long id, String userName, String note) {

        UserVo userVo = new UserVo(id, userName, note);
        activeMqUserService.sendUser(userVo);
        return result(true, userVo);
    }

    /**
     * 测试普通消息的发送
     */
    @GetMapping("/msg")
    public Map<String, Object> msg(String message) {
        activeMQService.sendMsg(message);
        return result(true, message);
    }

    /**
     * @param success
     * @param message
     * @return
     */
    private Map<String, Object> result(Boolean success, Object message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", message);
        return result;
    }
}
