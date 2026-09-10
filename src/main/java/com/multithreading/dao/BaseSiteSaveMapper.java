package com.multithreading.dao;

import java.util.List;

import com.multithreading.bo.*;
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

	void saveAntennaList(@Param("antennaList") List<Antenna> antennaList);

	void saveAAUList(@Param("aauList") List<AAUModel> aauModelList);

	void saveBBUList(@Param("bbuList") List<BBUModel> bbuModelList);

	void saveOilList(@Param("oilList") List<OilModel> oilModelList);

	void saveAirConditionList(@Param("airConditionList") List<AirCondition> airConditionList);

	void saveHoldingPoleList(@Param("holdingPoleList") List<HoldingPole> holdingPoleList);

	void saveRRUList(@Param("rruModelList") List<RRUModel> rruModelList);

	void savePowerList(@Param("powerList") List<Power> powerList);
}
