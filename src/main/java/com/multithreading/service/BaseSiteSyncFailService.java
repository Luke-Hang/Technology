package com.multithreading.service;

import com.multithreading.bo.BaseSiteSyncBO;
import com.multithreading.bo.District;

public interface BaseSiteSyncFailService {

    void saveFailRecord(String batchNo, District district, BaseSiteSyncBO baseSiteModel, Exception exception);
}
