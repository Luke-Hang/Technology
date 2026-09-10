package com.multithreading.entity;

import lombok.Data;

@Data
public class RRUEntity extends BaseSiteDeviceEntity {

    private String deviceCode;

    private String deviceName;

    private String brand;

    private String model;

    private String frequencyBand;

    private String installPosition;

    private String status;
}
