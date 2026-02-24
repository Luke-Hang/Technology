package com.cashgenerator.service;

import com.cashgenerator.AuthenticationType;
import com.cashgenerator.dao.AuthenticationMapper;
import com.cashgenerator.model.LoginBo;
import com.cashgenerator.model.UserAuthentication;
import com.cashgenerator.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerLoginServiceImpl implements CustomerLoginService{

    private static final Logger logger = LoggerFactory.getLogger(CustomerLoginServiceImpl.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationMapper authenticationMapper;
    @Override
    public LoginBo validateCustomer(LoginBo loginBo, AuthenticationType authenticationType) {
        if (loginBo != null) {
            createTokenList(loginBo);

        }
        return null;
    }

    private void createTokenList(LoginBo loginBo) {
        UserAuthentication authentication = new UserAuthentication();
        String jsonLoginBo = null;
        try {
            jsonLoginBo = new ObjectMapper().writeValueAsString(loginBo);
        } catch (JsonProcessingException e) {
            logger.error("");
            throw new RuntimeException(e);
        }
        //客户端 token,包含用户信息
        String customerAuthToken = jwtUtils.generateJwtToken(loginBo.getLoginName(), "customer_auth_token", jsonLoginBo);

        //服务端 token，标识服务权限
        String appAuthToken = jwtUtils.generateJwtToken(loginBo.getLoginName(), "app_auth_token", "customer_services");

        UUID uuid = UUID.randomUUID();
        authentication.setUserId(loginBo.getLoginId());
        authentication.setToken(uuid.toString());

        loginBo.setToken(authentication.getToken());

        authentication.setStoreId(loginBo.getStoreId());
        authentication.setValid(true);

        authenticationMapper.save(authentication);

        loginBo.setToken(uuid.toString());
        loginBo.setCustomerAuthToken(customerAuthToken);
        loginBo.setAppAuthToken(appAuthToken);

    }
}
