package com.multithreading.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multithreading.dao.BaseSiteSyncFailMapper;
import com.multithreading.entity.BaseSiteSyncFailEntity;
import com.multithreading.bo.BaseSiteSyncBO;
import com.multithreading.bo.District;
import com.multithreading.service.BaseSiteSyncFailService;
import org.apache.commons.lang3.StringUtils;
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
    public void saveFailRecord(String batchNo, District district, BaseSiteSyncBO baseSiteModel, Exception exception) {
        BaseSiteSyncFailEntity failRecord = new BaseSiteSyncFailEntity();
        failRecord.setBatchNo(batchNo);
        if (district != null) {
            failRecord.setDistrictCode(district.getDistrictNo());
            failRecord.setDistrictName(district.getDistrictName());
        }
        failRecord.setBaseSiteCode(StringUtils.defaultIfBlank(getBaseSiteCode(baseSiteModel), DEFAULT_BASE_SITE_CODE));
        failRecord.setBaseSiteName(getBaseSiteName(baseSiteModel));
        failRecord.setRawDataJson(toJson(baseSiteModel));
        failRecord.setErrorMessage(buildErrorMessage(exception));
        failRecord.setStatus(FAILED_STATUS);

        baseSiteSyncFailMapper.save(failRecord);
    }

    private String toJson(BaseSiteSyncBO baseSiteModel) {
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

    private String getBaseSiteCode(BaseSiteSyncBO baseSiteModel) {
        if (baseSiteModel == null) {
            return null;
        }
        return baseSiteModel.getBaseSiteCode();
    }

    private String getBaseSiteName(BaseSiteSyncBO baseSiteModel) {
        if (baseSiteModel == null) {
            return null;
        }
        return baseSiteModel.getBaseSiteName();
    }

}
