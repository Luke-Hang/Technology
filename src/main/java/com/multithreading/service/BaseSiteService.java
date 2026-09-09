package com.multithreading.service;

import java.util.List;

import com.multithreading.model.BaseSiteModel;
import com.multithreading.model.District;


public interface BaseSiteService {


	void baseSiteService(District district, List<BaseSiteModel> baseSiteList);
}
