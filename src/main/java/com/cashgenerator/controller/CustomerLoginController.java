package com.cashgenerator.controller;

import com.cashgenerator.AuthenticationType;
import com.cashgenerator.model.LoginBo;
import com.cashgenerator.service.CustomerLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RequestMapping("/login")
@RestController
public class CustomerLoginController {

    private final static Logger logger = LoggerFactory.getLogger(CustomerLoginController.class);


    @Autowired
    private CustomerLoginService customerLoginService;

    @PostMapping("/customer/login")
    //LoginBo 为用户登录信息
    public LoginBo login(@NotNull @Valid @RequestBody LoginBo loginBo,
                         @RequestParam(defaultValue = "Login_authentication") AuthenticationType authenticationType) {
        return customerLoginService.validateCustomer(loginBo, authenticationType);

    }
}
