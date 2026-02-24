package com.cashgenerator.model;

import lombok.Data;

@Data
public class LoginBo {
    private String loginId;
    private String cardNumber;
    private String password;
    private String loginName;
    private String token;
    private String customerAuthToken;
    private String appAuthToken;
    private Long storeId;
}
