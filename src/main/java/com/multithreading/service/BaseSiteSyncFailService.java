package com.multithreading.service;

import com.multithreading.model.BaseSiteSyncBO;
import com.multithreading.model.District;

public interface BaseSiteSyncFailService {

    void saveFailRecord(String batchNo, District district, BaseSiteSyncBO baseSiteModel, Exception exception);
}
