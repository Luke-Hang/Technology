package com.multithreading.dao;

import com.multithreading.entity.BaseSiteSyncFailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BaseSiteSyncFailMapper {

    void save(BaseSiteSyncFailEntity failRecord);

    List<BaseSiteSyncFailEntity> findByBatchNo(@Param("batchNo") String batchNo);

    void updateRetrySuccess(@Param("id") Long id);

    void updateRetryFailed(@Param("id") Long id, @Param("errorMessage") String errorMessage);
}
