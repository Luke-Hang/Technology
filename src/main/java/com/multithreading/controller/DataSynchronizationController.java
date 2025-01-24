package com.multithreading.controller;

import com.huawei.service.SmsService;
import com.multithreading.model.BaseSiteModel;
import com.multithreading.service.baseSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * 
 * @author xiehang
 *
 * 2021年1月31日
 * 数据同步实用类
 */
@RestController
@RequestMapping("/dataSync")
public class DataSynchronizationController {

	@Autowired
	private baseSiteService baseSiteService;
	
	@Autowired
	@Qualifier(value = "smsServiceImpl1")
	private SmsService smsService;


	/**
	 * 调用站点信息库接口获取数据
	 */
	@RequestMapping("/getDataSynchronization")
	public void getDataSynchronization() {
		//远程调用外部接口获取数据并将数据封装袋一个list中
		List<BaseSiteModel> list=getBaseSiteList();
		//将数据插入库中
		baseSiteService.baseSiteService(list);
	}

	private List<BaseSiteModel> getBaseSiteList() {
		return null;
	}
	
}
