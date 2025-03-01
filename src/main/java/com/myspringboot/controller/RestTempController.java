package com.myspringboot.controller;

import com.myspringboot.model.UserModel;
import com.myspringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author xiehang
 * @create 2022-08-01 16:51
 */
@RestController
@RequestMapping
public class RestTempController {

    private static final String url="localhost:8080/user";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    //增加post---
    // restTemplate.postForEntity
    @RequestMapping(value = "/insertuser", method = RequestMethod.POST)
    public void insertUser(@RequestBody UserModel userModel) {
        restTemplate.postForEntity("localhost:8080/user", getUserModelHttpEntity(userModel), UserModel.class);

    }

    //删除delete---
    // restTemplate.delete
    @RequestMapping(value = "/deleteuser",method = RequestMethod.GET)
    public void deleteUser(@RequestParam("id") String id){
        restTemplate.delete("localhost:8080/user/{id}", id);
    }

    //修改put---
    // restTemplate.put
    @RequestMapping(value = "/putuser",method = RequestMethod.POST)
    public void updateUser(@RequestBody UserModel userModel){
        restTemplate.put("localhost:8080/user", getUserModelHttpEntity(userModel));
    }

    //查询GET---
    // restTemplate.getForEntity
    @RequestMapping(value = "/getuser/{id}",method = RequestMethod.GET)
    public void getUser(@PathVariable("id") String id){
        restTemplate.getForEntity("localhost:8080/user/{id}", UserModel.class, id);
    }

    private static HttpEntity<UserModel> getUserModelHttpEntity(UserModel userModel) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new HttpEntity<>(userModel, httpHeaders);
    }
}
