package com.multithreading.model;

import lombok.Data;

@Data
public class BaseSiteSyncFail {

    private String batchNo;

    private String districtCode;

    private String districtName;

    private String baseSiteCode;

    private String baseSiteName;

    private String rawDataJson;

    private String errorMessage;

    private String status;
}
