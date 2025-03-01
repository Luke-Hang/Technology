package com.myspringboot.controller;

import com.myspringboot.model.UserModel;
import com.myspringboot.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xiehang
 * @create 2022-07-26 7:49
 */
@RestController
public class RedisController {

    @Autowired
    private RedisService redisService;

    @RequestMapping(value="/addstring")
    public void redisAddString(){
        redisService.addString();
    }

    @RequestMapping(value="/addlist")
    public void redisAddList(){
        redisService.addList();
    }

    @RequestMapping(value = "/getlistData")
    public List<UserModel> getList(){
        UserModel userModel=new UserModel();
        userModel.setUserId("1");
        List<UserModel> list = redisService.getListData();
        return list;
    }

}
