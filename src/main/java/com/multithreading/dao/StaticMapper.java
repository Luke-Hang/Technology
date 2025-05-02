package com.multithreading.dao;

import java.util.List;

import com.multithreading.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 
 * @author xiehang
 * 2021年1月31日
 *
 */
@Mapper
public interface StaticMapper {

	void saveAntennaBatch(@Param("antennaList") List<Antenna> antennaList);

	void saveAAUBatch(@Param("aauList") List<AAUModel> aauModelList);

	void saveBBUBatch(@Param("bbuList") List<BBUModel> bbuModelList);

	void saveOilDateBatch(@Param("oilList") List<OilModel> oilModelList);

	void saveAirConditionDataBatch(@Param("airConditionList") List<AirCondition> airConditionList);


}
