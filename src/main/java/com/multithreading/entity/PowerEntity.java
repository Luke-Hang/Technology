package com.multithreading.entity;

import lombok.Data;

@Data
public class PowerEntity extends BaseSiteDeviceEntity {

    private String deviceCode;

    private String deviceName;

    private String brand;

    private String model;

    private String powerType;

    private String capacity;

    private String status;
}
