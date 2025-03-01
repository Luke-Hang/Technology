package com.myspringboot;

import com.myspringboot.model.UserModel;
import com.myspringboot.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Chapter2ApplicationTests {

    @Autowired(required = true)
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testGetUser() {
//        UserModel userModel= userService.getUser(1L);
//        //判断用户信息是否为空
//        Assert.assertNotNull(userModel);
        System.out.println(111);
    }
}
