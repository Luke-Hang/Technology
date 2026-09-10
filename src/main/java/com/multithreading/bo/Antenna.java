package com.multithreading.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xiehang
 * @date 2025/5/2 9:40
 */
@Data
public class Antenna {

    private String baseSiteCode;
    private String baseSiteName;
    private String deviceCode;
    private String deviceName;
    private String brand;
    private String model;
    private String antennaType;
    private BigDecimal directionAngle;
    private BigDecimal downTilt;
    private String installPosition;
    private String status;
}
