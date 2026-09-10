package com.multithreading.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BaseSiteEntity {

    private Long id;

    private String baseSiteCode;

    private String baseSiteName;

    private String districtCode;

    private String districtName;

    private String address;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String siteType;

    private String networkType;

    private String vendor;

    private String status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
