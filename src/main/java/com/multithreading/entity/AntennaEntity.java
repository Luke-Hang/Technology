package com.multithreading.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AntennaEntity {

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
