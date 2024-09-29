package com.myspringboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.myspringboot.model.UserModel;

/**
 * @author xiehang
 * @create 2022-08-01 16:14
 */
@RestController
@RequestMapping
public class MongoController {
    @Autowired
    private MongoTemplate mongoTemplate;

    public UserModel getUser(){
        return null;
    }
}
