package com.cashgenerator.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserAuthentication {
    private String userId;
    private Long storeId;
    private String token;
    private Boolean valid;
    private Timestamp invalidateDate;

}
