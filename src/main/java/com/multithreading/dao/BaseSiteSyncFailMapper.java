package com.multithreading.dao;

import com.multithreading.model.BaseSiteSyncFail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BaseSiteSyncFailMapper {

    @Insert({
            "insert into site5g.base_site_sync_fail(",
            "batch_no, district_code, district_name, base_site_code, base_site_name,",
            "raw_data_json, error_message, status",
            ") values (",
            "#{batchNo}, #{districtCode}, #{districtName}, #{baseSiteCode}, #{baseSiteName},",
            "#{rawDataJson}, #{errorMessage}, #{status}",
            ")"
    })
    void save(BaseSiteSyncFail failRecord);
}
