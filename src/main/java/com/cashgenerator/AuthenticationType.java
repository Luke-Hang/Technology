package com.cashgenerator;


import lombok.Data;

import java.security.PrivateKey;
public enum AuthenticationType {
    LOGIN_AUTHENTICATION("authentication"),
    TRANSACTION_AUTHENTICATION("Transaction_Authentication");

    private String Authentication;
    private String Transaction_Authentication;

    AuthenticationType(String authentication) {
        Authentication = authentication;
    }
}
