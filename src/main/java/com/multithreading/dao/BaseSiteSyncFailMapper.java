package com.multithreading.dao;

import com.multithreading.entity.BaseSiteSyncFailEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BaseSiteSyncFailMapper {

    void save(BaseSiteSyncFailEntity failRecord);
}
