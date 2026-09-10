package com.multithreading.bo;

import lombok.Data;

/**
 * @author xiehang
 * @date 2025/5/2 11:37
 */
@Data
public class RRUModel extends BaseSiteDeviceBO {

    private String deviceCode;
    private String deviceName;
    private String brand;
    private String model;
    private String frequencyBand;
    private String installPosition;
    private String status;
}
