package com.multithreading.model;

import lombok.Data;

import java.util.List;

@Data
public class BaseSiteModel {

    //空调
    List<AirCondition> airConditionList;

    //油机
    List<OilModel> oilModelList;

    //AAU
    List<AAUModel> aauModelList;

    //BBU
    List<BBUModel> bbuModelList;

    //RRU
    List<RRUModel> rruModelList;

    //电池
    List<Power> powerList;

    //天线
    List<Antenna> antennaList;

    //抱杆
    List<HoldingPole> holdingPoleList;
}
