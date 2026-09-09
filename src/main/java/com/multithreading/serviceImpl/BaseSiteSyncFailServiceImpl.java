package com.multithreading.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multithreading.dao.BaseSiteSyncFailMapper;
import com.multithreading.model.AAUModel;
import com.multithreading.model.AirCondition;
import com.multithreading.model.Antenna;
import com.multithreading.model.BBUModel;
import com.multithreading.model.BaseSiteModel;
import com.multithreading.model.BaseSiteSyncFail;
import com.multithreading.model.District;
import com.multithreading.model.HoldingPole;
import com.multithreading.model.OilModel;
import com.multithreading.model.Power;
import com.multithreading.model.RRUModel;
import com.multithreading.service.BaseSiteSyncFailService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BaseSiteSyncFailServiceImpl implements BaseSiteSyncFailService {

    private static final String DEFAULT_BASE_SITE_CODE = "UNKNOWN";
    private static final String FAILED_STATUS = "FAILED";

    @Autowired
    private BaseSiteSyncFailMapper baseSiteSyncFailMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveFailRecord(String batchNo, District district, BaseSiteModel baseSiteModel, Exception exception) {
        SiteInfo siteInfo = extractSiteInfo(baseSiteModel);

        BaseSiteSyncFail failRecord = new BaseSiteSyncFail();
        failRecord.setBatchNo(batchNo);
        if (district != null) {
            failRecord.setDistrictCode(district.getDistrictNo());
            failRecord.setDistrictName(district.getDistrictName());
        }
        failRecord.setBaseSiteCode(defaultString(siteInfo.baseSiteCode, DEFAULT_BASE_SITE_CODE));
        failRecord.setBaseSiteName(siteInfo.baseSiteName);
        failRecord.setRawDataJson(toJson(baseSiteModel));
        failRecord.setErrorMessage(buildErrorMessage(exception));
        failRecord.setStatus(FAILED_STATUS);

        baseSiteSyncFailMapper.save(failRecord);
    }

    private String toJson(BaseSiteModel baseSiteModel) {
        try {
            return objectMapper.writeValueAsString(baseSiteModel);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialize failed base site data error", e);
        }
    }

    private String buildErrorMessage(Exception exception) {
        if (exception == null) {
            return null;
        }
        return exception.getClass().getName() + ": " + exception.getMessage();
    }

    private SiteInfo extractSiteInfo(BaseSiteModel baseSiteModel) {
        if (baseSiteModel == null) {
            return new SiteInfo(null, null);
        }
        if (CollectionUtils.isNotEmpty(baseSiteModel.getAirConditionList())) {
            AirCondition first = baseSiteModel.getAirConditionList().get(0);
            return new SiteInfo(first.getBaseSiteCode(), first.getBaseSiteName());
        }
        if (CollectionUtils.isNotEmpty(baseSiteModel.getOilModelList())) {
            OilModel first = baseSiteModel.getOilModelList().get(0);
            return new SiteInfo(first.getBaseSiteCode(), first.getBaseSiteName());
        }
        if (CollectionUtils.isNotEmpty(baseSiteModel.getAauModelList())) {
            AAUModel first = baseSiteModel.getAauModelList().get(0);
            return new SiteInfo(first.getBaseSiteCode(), first.getBaseSiteName());
        }
        if (CollectionUtils.isNotEmpty(baseSiteModel.getBbuModelList())) {
            BBUModel first = baseSiteModel.getBbuModelList().get(0);
            return new SiteInfo(first.getBaseSiteCode(), first.getBaseSiteName());
        }
        if (CollectionUtils.isNotEmpty(baseSiteModel.getRruModelList())) {
            RRUModel first = baseSiteModel.getRruModelList().get(0);
            return new SiteInfo(first.getBaseSiteCode(), first.getBaseSiteName());
        }
        if (CollectionUtils.isNotEmpty(baseSiteModel.getAntennaList())) {
            Antenna first = baseSiteModel.getAntennaList().get(0);
            return new SiteInfo(first.getBaseSiteCode(), first.getBaseSiteName());
        }
        if (CollectionUtils.isNotEmpty(baseSiteModel.getHoldingPoleList())) {
            HoldingPole first = baseSiteModel.getHoldingPoleList().get(0);
            return new SiteInfo(first.getBaseSiteCode(), first.getBaseSiteName());
        }
        if (CollectionUtils.isNotEmpty(baseSiteModel.getPowerList())) {
            Power first = baseSiteModel.getPowerList().get(0);
            return new SiteInfo(first.getBaseSiteCode(), first.getBaseSiteName());
        }
        return new SiteInfo(null, null);
    }

    private String defaultString(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value;
    }

    private static class SiteInfo {
        private final String baseSiteCode;
        private final String baseSiteName;

        private SiteInfo(String baseSiteCode, String baseSiteName) {
            this.baseSiteCode = baseSiteCode;
            this.baseSiteName = baseSiteName;
        }
    }
}
