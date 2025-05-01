package com.multithreading.model;

import lombok.Data;

import java.util.List;

@Data
public class BaseSiteModel {

    String siteId;

    OilModel oilModel;

    AirCondition airCondition;

    AAUModel aauModel;

}
