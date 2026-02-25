package com.cashgenerator.service;

import com.cashgenerator.AuthenticationType;
import com.cashgenerator.dao.AuthenticationMapper;
import com.cashgenerator.model.LoginBo;
import com.cashgenerator.model.LoginUserInfo;
import com.cashgenerator.model.UserAuthentication;
import com.cashgenerator.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class CustomerLoginServiceImpl implements CustomerLoginService{

    private static final Logger logger = LoggerFactory.getLogger(CustomerLoginServiceImpl.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationMapper authenticationMapper;
    @Override
    public LoginBo validateCustomerCredentials(LoginBo loginBo, AuthenticationType authenticationType) {
        if (loginBo != null) {
            createTokenList(loginBo);
            LoginUserInfo loginUserInfo = getLoginUserInfo();
            loginBo.setLoginUserInfo(loginUserInfo);
        }
        /**
         * **响应内容** (`LoginBo`对象):
         * ```json
         * {
         *   "loginId": "user123",
         *   "storeId": "1",
         *   "status": true,
         *   "message": "OK",
         *   "token": "550e8400-e29b-41d4-a716-446655440000",  // UUID Token
         *   "customerAuthToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // JWT Token 1
         *   "appAuthToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",        // JWT Token 2
         *   "tillsBoList": [...],
         *   "loginUserInfo": {
         *     "loginName": "user123",
         *     "role": "CASHIER",
         *     "operations": ["SALE", "REFUND", ...]
         *   },
         *   "password": ""  // 已清空
         * }
         */
        return loginBo;
    }

    private void createTokenList(LoginBo loginBo) {
        UserAuthentication authentication = new UserAuthentication();
        String jsonLoginBo = null;
        try {
            jsonLoginBo = new ObjectMapper().writeValueAsString(loginBo);
        } catch (JsonProcessingException e) {
            logger.error("Error while converting object to JSON: {}", loginBo, e);
            throw new RuntimeException(e);
        }

        //客户端 token,包含用户信息
        // Subject: 用户登录名，Claim名称，Claim值: LoginBo的JSON字符串
        /**
         *  **生成的Token结构**:
         * ```json
         * {
         *   "sub": "user123",
         *   "customer_auth_token": "{\"loginId\":\"user123\",\"storeId\":\"1\",...}",
         *   "iat": 1704067200000,
         *   "exp": 1704153600000
         * }
         */
        String customerAuthToken = jwtUtils.generateJwtToken(loginBo.getLoginName(), "customer_auth_token", jsonLoginBo);


        //服务端 token，标识服务权限
        // Subject: 用户登录名   Claim名称 Claim值: 固定应用标识
        /**
         * **生成的Token结构**:
         * ```json
         * {
         *   "sub": "user123",
         *   "app_auth_token": "customer_services",
         *   "iat": 1704067200000,
         *   "exp": 1704153600000
         * }
         */
        String appAuthToken = jwtUtils.generateJwtToken(loginBo.getLoginName(), "app_auth_token", "customer_services");


        //将appAuthToken放入MDC
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        contextMap.put("appAuthToken", appAuthToken);
        MDC.setContextMap(contextMap);

        //生成UUID Token并保存到数据库
        UUID uuid = UUID.randomUUID();
        authentication.setUserId(loginBo.getLoginId());
        authentication.setToken(uuid.toString());// UUID Token
        loginBo.setToken(authentication.getToken());
        authentication.setStoreId(loginBo.getStoreId());
        authentication.setValid(true);
        authenticationMapper.save(authentication);// 保存到数据库

        //设置 loginBo 的 token 信息
        loginBo.setToken(uuid.toString());// UUID Token
        loginBo.setCustomerAuthToken(customerAuthToken);// JWT Token 1
        loginBo.setAppAuthToken(appAuthToken);// JWT Token 2
    }

    private LoginUserInfo getLoginUserInfo() {
        LoginUserInfo loginUserInfo = new LoginUserInfo();
        return loginUserInfo;
    }
}
