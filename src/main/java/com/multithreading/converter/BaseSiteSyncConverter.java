package com.multithreading.converter;

import com.multithreading.bo.AAUModel;
import com.multithreading.bo.AirCondition;
import com.multithreading.bo.Antenna;
import com.multithreading.bo.BBUModel;
import com.multithreading.bo.BaseSiteSyncBO;
import com.multithreading.bo.HoldingPole;
import com.multithreading.bo.OilModel;
import com.multithreading.bo.Power;
import com.multithreading.bo.RRUModel;
import com.multithreading.entity.AAUEntity;
import com.multithreading.entity.AirConditionEntity;
import com.multithreading.entity.AntennaEntity;
import com.multithreading.entity.BBUEntity;
import com.multithreading.entity.BaseSiteEntity;
import com.multithreading.entity.HoldingPoleEntity;
import com.multithreading.entity.OilEntity;
import com.multithreading.entity.PowerEntity;
import com.multithreading.entity.RRUEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BaseSiteSyncConverter {

    BaseSiteEntity toBaseSiteEntity(BaseSiteSyncBO baseSiteSyncBO);

    List<AirConditionEntity> toAirConditionEntityList(List<AirCondition> airConditionList);

    List<OilEntity> toOilEntityList(List<OilModel> oilModelList);

    List<AAUEntity> toAAUEntityList(List<AAUModel> aauModelList);

    List<BBUEntity> toBBUEntityList(List<BBUModel> bbuModelList);

    List<RRUEntity> toRRUEntityList(List<RRUModel> rruModelList);

    List<AntennaEntity> toAntennaEntityList(List<Antenna> antennaList);

    List<HoldingPoleEntity> toHoldingPoleEntityList(List<HoldingPole> holdingPoleList);

    List<PowerEntity> toPowerEntityList(List<Power> powerList);
}
