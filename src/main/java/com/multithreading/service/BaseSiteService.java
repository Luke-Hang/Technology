package com.multithreading.service;

import java.util.List;

import com.multithreading.model.BaseSiteSyncBO;
import com.multithreading.model.District;


public interface BaseSiteService {


	void baseSiteService(District district, List<BaseSiteSyncBO> baseSiteList);
}
