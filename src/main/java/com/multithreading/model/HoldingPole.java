package com.multithreading.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xiehang
 * @date 2025/5/2 11:34
 */
@Data
public class HoldingPole {

    private String baseSiteCode;
    private String baseSiteName;
    private String poleCode;
    private String poleName;
    private String poleType;
    private BigDecimal height;
    private String installPosition;
    private String status;
}
