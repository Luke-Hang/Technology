package com.cashgenerator.model;

import lombok.Data;

@Data
public class LoginUserInfo {
    private Long id;
    private Long storeId;
    private String foreName;
    private String surName;
}
