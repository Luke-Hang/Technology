package com.multithreading.bo;

import lombok.Data;

/**
 * @author xiehang
 * @date 2025/5/1 22:47
 */
@Data
public class AirCondition extends BaseSiteDeviceBO {

    private String deviceCode;
    private String deviceName;
    private String brand;
    private String model;
    private String installPosition;
    private String status;
}
