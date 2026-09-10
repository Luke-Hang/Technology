package com.multithreading.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entity  -> 数据库表映射
 * DTO     -> 接口入参/出参、服务间传输
 * VO      -> 前端展示对象
 * BO      -> 业务对象/业务过程中的对象
 * Query   -> 查询条件对象
 * Command -> 写操作请求对象
 */
@Data
public class BaseSiteSyncFailEntity {

    private Long id;

    private String batchNo;

    private String districtCode;

    private String districtName;

    private String baseSiteCode;

    private String baseSiteName;

    private String rawDataJson;

    private String errorMessage;

    private Integer retryCount;

    private String status;

    private LocalDateTime firstFailTime;

    private LocalDateTime lastRetryTime;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
