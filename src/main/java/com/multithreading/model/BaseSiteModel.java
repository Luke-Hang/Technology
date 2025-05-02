package com.multithreading.model;

import lombok.Data;

import java.util.List;

@Data
public class BaseSiteModel {

    List<Antenna> antennaList;

    //AAU
    List<AAUModel> aauModelList;

    //AAU
    List<BBUModel> bbuModelList;

    //油机
    List<OilModel> oilModelList;

    //空调
    List<AirCondition> airConditionList;
}
