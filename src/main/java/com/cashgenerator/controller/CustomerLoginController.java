package com.cashgenerator.controller;

import com.cashgenerator.AuthenticationType;
import com.cashgenerator.model.Customer;
import com.cashgenerator.model.LoginBo;
import com.cashgenerator.service.CustomerLoginService;
import com.cashgenerator.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping("/login")
@RestController
public class CustomerLoginController {

    private final static Logger logger = LoggerFactory.getLogger(CustomerLoginController.class);


    @Autowired
    private CustomerLoginService customerLoginService;

    @Autowired
    private CustomerService customerService;

    //一、登录阶段 - JWT Token生成流程

    /**
     * **前端操作**:
     * ```javascript
     * // 前端点击登录按钮
     * POST /customer/api/v1/validateCustomerCredentials
     * Content-Type: application/json
     *  loginBo json
     * {
     *   "loginId": "user123",
     *   "password": "password123",
     *   "storeId": "1",
     *   "cardNumber": "1234",
     *   "ipaddress": "192.168.1.100",
     *   "macaddress": "00:11:22:33:44:55",
     *   "fingerPrintHash": "false"
     * }
     */
    @PostMapping("/customer/login")
    //LoginBo 为用户登录信息
    public LoginBo login(@NotNull @Valid @RequestBody LoginBo loginBo,
                         @RequestParam(defaultValue = "Login_authentication") AuthenticationType authenticationType) {
        return customerLoginService.validateCustomerCredentials(loginBo, authenticationType);
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
        /**
         * **前端操作**:
         * ```javascript
         * // 前端收到响应后
         * const response = await loginAPI(credentials);
         *
         * // 存储Token到localStorage或内存
         * localStorage.setItem('customerAuthToken', response.customerAuthToken);
         * localStorage.setItem('appAuthToken', response.appAuthToken);
         * localStorage.setItem('uuidToken', response.token);
         *
         */
    }

    //二、后续请求阶段 - JWT Token验证流程
    /**
     * 1. 前端发起业务请求
     *
     * **前端操作**:
     * ```javascript
     * // 前端发起业务请求（例如：获取客户列表）
     * GET /customer/api/v1/customers
     * Headers: {
     *   "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // 使用appAuthToken或customerAuthToken
     *   "X-Store-Id": "1",
     *   "X-Till-Id": "2",
     *   "X-User-Name": "user123"
     * }
     * ```
     *
     * **注意**: 通常使用`appAuthToken`作为Authorization头，因为它是应用级别的Token。
     *
     * 详见 JwtTokenFilter.java
     */


    @GetMapping("/customers")
    public List<Customer> getCustomers() {
        // 方式1: 从SecurityContext获取
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String username = userDetails.getUsername();

        // 方式2: 从MDC获取（如果之前设置了）
        String storeId = MDC.get("storeId");
        String userName = MDC.get("userName");

        // 执行业务逻辑
        return customerService.getCustomers(storeId);
    }
}
