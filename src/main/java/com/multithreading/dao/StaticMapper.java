package com.multithreading.dao;

import java.util.List;

import com.multithreading.model.BaseSiteModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author xiehang
 * 2021年1月31日
 *
 */
@Mapper
public interface StaticMapper {

	void saveDta(List<BaseSiteModel> listMap2);

}
