package com.multithreading.service;

import java.util.List;

import com.multithreading.bo.BaseSiteSyncBO;
import com.multithreading.bo.District;


public interface BaseSiteService {


	void baseSiteService(District district, List<BaseSiteSyncBO> baseSiteList);
}
