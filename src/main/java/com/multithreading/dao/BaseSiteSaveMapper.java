package com.multithreading.dao;

import java.util.List;

import com.multithreading.entity.AAUEntity;
import com.multithreading.entity.AirConditionEntity;
import com.multithreading.entity.AntennaEntity;
import com.multithreading.entity.BBUEntity;
import com.multithreading.entity.BaseSiteEntity;
import com.multithreading.entity.HoldingPoleEntity;
import com.multithreading.entity.OilEntity;
import com.multithreading.entity.PowerEntity;
import com.multithreading.entity.RRUEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * @author xiehang
 * 2021年1月31日
 *
 */
@Mapper
public interface BaseSiteSaveMapper {

	void saveBaseSite(BaseSiteEntity baseSiteEntity);

	void saveAntennaList(@Param("antennaList") List<AntennaEntity> antennaList);

	void saveAAUList(@Param("aauList") List<AAUEntity> aauModelList);

	void saveBBUList(@Param("bbuList") List<BBUEntity> bbuModelList);

	void saveOilList(@Param("oilList") List<OilEntity> oilModelList);

	void saveAirConditionList(@Param("airConditionList") List<AirConditionEntity> airConditionList);

	void saveHoldingPoleList(@Param("holdingPoleList") List<HoldingPoleEntity> holdingPoleList);

	void saveRRUList(@Param("rruModelList") List<RRUEntity> rruModelList);

	void savePowerList(@Param("powerList") List<PowerEntity> powerList);
}
