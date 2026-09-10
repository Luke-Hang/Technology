package com.multithreading.service;

import com.multithreading.bo.BaseSiteSyncBO;
import com.multithreading.bo.District;
import com.multithreading.entity.BaseSiteSyncFailEntity;

import java.util.List;

public interface BaseSiteSyncFailService {

    void saveFailRecord(String batchNo, District district, BaseSiteSyncBO baseSiteModel, Exception exception);

    List<BaseSiteSyncFailEntity> findFailRecords(String batchNo);

    int retryFailRecords(String batchNo);
}
