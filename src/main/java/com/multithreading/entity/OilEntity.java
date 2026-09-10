package com.multithreading.entity;

import lombok.Data;

@Data
public class OilEntity extends BaseSiteDeviceEntity {

    private String deviceCode;

    private String deviceName;

    private String brand;

    private String model;

    private String powerCapacity;

    private String status;
}
