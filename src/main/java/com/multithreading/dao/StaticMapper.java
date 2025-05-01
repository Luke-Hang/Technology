package com.multithreading.dao;

import java.util.List;

import com.multithreading.model.AAUModel;
import com.multithreading.model.AirCondition;
import com.multithreading.model.BaseSiteModel;
import com.multithreading.model.OilModel;
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

	void saveOilDateBatch(@Param("oilList") List<OilModel> oilModelList);

	void saveAirConditionDataBatch(@Param("airConditionList") List<AirCondition> airConditionList);

	void saveAAUBatch(@Param("aauList") List<AAUModel> aauModelList);
}
