package com.multithreading.service;

import com.multithreading.model.BaseSiteModel;
import com.multithreading.model.District;

public interface BaseSiteSyncFailService {

    void saveFailRecord(String batchNo, District district, BaseSiteModel baseSiteModel, Exception exception);
}
