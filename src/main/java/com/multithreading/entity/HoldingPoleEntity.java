package com.multithreading.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HoldingPoleEntity {

    private String baseSiteCode;

    private String baseSiteName;

    private String poleCode;

    private String poleName;

    private String poleType;

    private BigDecimal height;

    private String installPosition;

    private String status;
}
