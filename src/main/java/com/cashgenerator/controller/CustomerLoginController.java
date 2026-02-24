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
        //返回响应给前端
/*
        {
            "loginid": "user123"
            "storeid": "1"
            "message": "ok"
            "token": "550e8400-e29b-41d4-a716-446655440000"
            "customerAuthToken":“550e8400-e29b-41d4-a716-446655440000”
            “appAuthToken”：“550e8400-e29b-41d4-a716-446655440000”
            “loginUserInfo”:{
                loginName:user123
                .....
            }
        }
        */

        //前端存储 Token
        /*
        localStoreage.setItem("customerAuthToken","response.customerAuthToken");
        localStoreage.setItem("appAuthToken","response.appAuthToken");
        localStoreage.setItem("uuidToken","response.uuidToken");
        */

        //前端发起请求
        /*
        GET /cistomer/api/vi/customers
        Headers:{
         "Authorization":"Bearer 550e8400-e29b-41d4-a716-446655440000"// 使用 appAuthToken 或者 customerAuthToken
         “X-Store-Id”:1
         “X-User-Name”: user123
        }
        通常使用 appAuthToken 作为Authorization头，因为他是应用级的token
        * */

    }
}
