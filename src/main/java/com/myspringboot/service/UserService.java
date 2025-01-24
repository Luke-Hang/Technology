package com.myspringboot.service;

import com.myspringboot.model.UserModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author xiehang
 * @create 2022-08-09 22:32
 */
//指定服务ID(Service ID)
@FeignClient("user")
public interface UserService {

    @GetMapping("/timeout")
    public String testTimeOut();

    @GetMapping("/user/{id}")
    public UserModel getUser(@PathVariable("id") Long id);

    @PostMapping("/insert")
    public Map<String,Object> addUser(@RequestBody UserModel userModel);

    @PostMapping("/update/{userName}")
    public Map<String,Object> updateName(@PathVariable("userName") String userName);

    UserModel findUser(String id);

//    List
}
