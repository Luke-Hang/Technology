package com.multithreading.bo;

import lombok.Data;

import java.util.List;
/**
 * Entity  -> 数据库表映射
 * DTO     -> 接口入参/出参、服务间传输
 * VO      -> 前端展示对象
 * BO      -> 业务对象/业务过程中的对象
 * Query   -> 查询条件对象
 * Command -> 写操作请求对象
 */
@Data
public class BaseSiteSyncBO {

    private String baseSiteCode;

    private String baseSiteName;

    private String districtCode;

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
