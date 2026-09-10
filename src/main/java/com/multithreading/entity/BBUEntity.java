package com.multithreading.entity;

import lombok.Data;

@Data
public class BBUEntity extends BaseSiteDeviceEntity {

    private String deviceCode;

    private String deviceName;

    private String brand;

    private String model;

    private String roomName;

    private String cabinetNo;

    private String status;
}
