package com.multithreading.model;

import lombok.Data;

import java.util.List;

@Data
public class BaseSiteModel {

    //天线
    List<Antenna> antennaList;

    //抱杆
    List<HoldingPole> holdingPoleList;

    //AAU
    List<AAUModel> aauModelList;

    //BBU
    List<BBUModel> bbuModelList;

    //RRu
    List<RRUModel> rruModelList;

    //电池
    List<Power> powerList;

    //油机
    List<OilModel> oilModelList;

    //空调
    List<AirCondition> airConditionList;
}
