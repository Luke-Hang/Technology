package com.multithreading.serviceImpl;

import com.multithreading.dao.BaseSiteSaveMapper;
import com.multithreading.bo.BaseSiteSyncBO;
import com.multithreading.converter.BaseSiteSyncConverter;
import com.multithreading.service.BaseSiteSaveService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class BaseSiteSaveServiceImpl implements BaseSiteSaveService {

    @Autowired
    private BaseSiteSaveMapper baseSiteSaveMapper;

    @Autowired
    private BaseSiteSyncConverter baseSiteSyncConverter;

    /**
     * 数据批量入库
     * baseSiteModel 一个基站
     * 每个小 list = 这个基站下某一类设备
     *      每次 insert 的 list 大小不是“区域内所有基站数量”，而是“单个基站下某类设备数量”。
     *      比如一个基站通常可能是：
     *      antennaList：几个到几十个
     *      holdingPoleList：1 到几个
     *      aauModelList：几个到几十个
     *      bbuModelList：1 到几个
     *      rruModelList：几个到几十个
     *      powerList：几个
     *      airConditionList：几个
     *      oilModelList：0 到几个
     * 所以单个小 list 一般很难超过 500
     *
     */
    @Override
    public void saveBaseSiteData(BaseSiteSyncBO synBaseSiteModel) {
        baseSiteSaveMapper.saveBaseSite(baseSiteSyncConverter.toBaseSiteEntity(synBaseSiteModel));

        //空调
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getAirConditionList())) {
            baseSiteSaveMapper.saveAirConditionList(baseSiteSyncConverter.toAirConditionEntityList(synBaseSiteModel.getAirConditionList()));
        }
        //油机
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getOilModelList())) {
            baseSiteSaveMapper.saveOilList(baseSiteSyncConverter.toOilEntityList(synBaseSiteModel.getOilModelList()));
        }
        //5G AAU
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getAauModelList())) {
            baseSiteSaveMapper.saveAAUList(baseSiteSyncConverter.toAAUEntityList(synBaseSiteModel.getAauModelList()));
        }
        //5G BBU
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getBbuModelList())) {
            baseSiteSaveMapper.saveBBUList(baseSiteSyncConverter.toBBUEntityList(synBaseSiteModel.getBbuModelList()));
        }
        //5G RRU
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getRruModelList())) {
            baseSiteSaveMapper.saveRRUList(baseSiteSyncConverter.toRRUEntityList(synBaseSiteModel.getRruModelList()));
        }
        //天线
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getAntennaList())) {
            baseSiteSaveMapper.saveAntennaList(baseSiteSyncConverter.toAntennaEntityList(synBaseSiteModel.getAntennaList()));
        }
        //抱杆
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getHoldingPoleList())) {
            baseSiteSaveMapper.saveHoldingPoleList(baseSiteSyncConverter.toHoldingPoleEntityList(synBaseSiteModel.getHoldingPoleList()));
        }
        //电源
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getPowerList())) {
            baseSiteSaveMapper.savePowerList(baseSiteSyncConverter.toPowerEntityList(synBaseSiteModel.getPowerList()));
        }
    }
}
