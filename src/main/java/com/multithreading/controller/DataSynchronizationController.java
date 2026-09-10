package com.multithreading.controller;

import com.multithreading.bo.BaseSiteSyncBO;
import com.multithreading.bo.District;
import com.multithreading.entity.BaseSiteSyncFailEntity;
import com.multithreading.service.SmsService;
import com.multithreading.service.BaseSiteService;
import com.multithreading.service.BaseSiteSyncFailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author xiehang
 * <p>
 * 2021年1月31日
 * 数据同步实用类
 */
@RestController
@RequestMapping("/dataSync")
public class DataSynchronizationController {

    private static final Logger logger = LoggerFactory.getLogger(DataSynchronizationController.class);

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private BaseSiteSyncFailService baseSiteSyncFailService;

    @Autowired
    //@Qualifier 存在多个相同类型的bean时，明确指定应该注入哪个具体的bean
    @Qualifier(value = "smsServiceImpl")
    private SmsService smsService;

    /**
     * 远程调用外部站点信息库接口，
     * 获取某个行政区域下的所有基站信息 即 baseSiteList
     *      baseSiteList = 某个行政区域下的所有基站
     *      每个 BaseSiteModel = 该行政区域下的一个基站
     *      每个小 list = 这个基站下某一类设备
     *          比如一个基站通常可能是：
     *      *      antennaList：几个到几十个
     *      *      holdingPoleList：1 到几个
     *      *      aauModelList：几个到几十个
     *      *      bbuModelList：1 到几个
     *      *      rruModelList：几个到几十个
     *      *      powerList：几个
     *      *      airConditionList：几个
     *      *      oilModelList：0 到几个
     * 所以单个小 list 一般很难超过 500，可以直接使用 mybatis 进行数据批量插入
     *
     */
    @RequestMapping("/getDataSynchronization")
    public void getDataSynchronization(District district) {
        // 远程调用外部站点信息库接口,获取某个区域下的所有基站信息
        // 讲个基站信息数据封装成一个大 BaseSiteModel List
        List<BaseSiteSyncBO> baseSiteList = getBaseSiteList(district);
        //将数据插入库中
        baseSiteService.baseSiteService(district, baseSiteList);
    }

    @GetMapping("/failRecords")
    public List<BaseSiteSyncFailEntity> failRecords(@RequestParam String batchNo) {
        return baseSiteSyncFailService.findFailRecords(batchNo);
    }

    @PostMapping("/retryFailRecords")
    public String retryFailRecords(@RequestParam String batchNo) {
        int successCount = baseSiteSyncFailService.retryFailRecords(batchNo);
        return "retry success count: " + successCount;
    }

    
    /**
     * 调用站点信息库接口获取某个区域下面的所有基站信息
     * @param district
     * @return
     */
    private List<BaseSiteSyncBO> getBaseSiteList(District district) {
//        District district = new District("01", "雁塔区");
        logger.info("获取" + district.getDistrictName() + "所有基站信息，开始！");

        logger.info("获取" + district.getDistrictName() + "所有基站信息，结束！");
        return new ArrayList<>();
    }

}
