package com.multithreading.serviceImpl;

import com.multithreading.dao.BaseSiteSaveMapper;
import com.multithreading.bo.BaseSiteSyncBO;
import com.multithreading.converter.BaseSiteSyncConverter;
import com.multithreading.entity.*;
import com.multithreading.service.BaseSiteSaveService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        BaseSiteEntity baseSiteEntity = baseSiteSyncConverter.toBaseSiteEntity(synBaseSiteModel);
        baseSiteSaveMapper.saveBaseSite(baseSiteEntity);

        //空调
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getAirConditionList())) {
            List<AirConditionEntity> airConditionEntityList = baseSiteSyncConverter.toAirConditionEntityList(synBaseSiteModel.getAirConditionList());
            baseSiteSaveMapper.saveAirConditionList(airConditionEntityList);
        }
        //油机
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getOilModelList())) {
            List<OilEntity> oilEntityList = baseSiteSyncConverter.toOilEntityList(synBaseSiteModel.getOilModelList());
            baseSiteSaveMapper.saveOilList(oilEntityList);
        }
        //5G AAU
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getAauModelList())) {
            List<AAUEntity> aauEntityList = baseSiteSyncConverter.toAAUEntityList(synBaseSiteModel.getAauModelList());
            baseSiteSaveMapper.saveAAUList(aauEntityList);
        }
        //5G BBU
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getBbuModelList())) {
            List<BBUEntity> bbuEntityList = baseSiteSyncConverter.toBBUEntityList(synBaseSiteModel.getBbuModelList());
            baseSiteSaveMapper.saveBBUList(bbuEntityList);
        }
        //5G RRU
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getRruModelList())) {
            List<RRUEntity> rruEntityList = baseSiteSyncConverter.toRRUEntityList(synBaseSiteModel.getRruModelList());
            baseSiteSaveMapper.saveRRUList(rruEntityList);
        }
        //天线
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getAntennaList())) {
            List<AntennaEntity> antennaEntityList = baseSiteSyncConverter.toAntennaEntityList(synBaseSiteModel.getAntennaList());
            baseSiteSaveMapper.saveAntennaList(antennaEntityList);
        }
        //抱杆
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getHoldingPoleList())) {
            List<HoldingPoleEntity> holdingPoleEntityList = baseSiteSyncConverter.toHoldingPoleEntityList(synBaseSiteModel.getHoldingPoleList());
            baseSiteSaveMapper.saveHoldingPoleList(holdingPoleEntityList);
        }
        //电源
        if (CollectionUtils.isNotEmpty(synBaseSiteModel.getPowerList())) {
            List<PowerEntity> powerEntityList = baseSiteSyncConverter.toPowerEntityList(synBaseSiteModel.getPowerList());
            baseSiteSaveMapper.savePowerList(powerEntityList);
        }
    }
}
