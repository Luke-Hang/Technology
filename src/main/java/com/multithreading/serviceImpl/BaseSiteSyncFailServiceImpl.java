package com.multithreading.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multithreading.dao.BaseSiteSyncFailMapper;
import com.multithreading.entity.BaseSiteSyncFailEntity;
import com.multithreading.bo.BaseSiteSyncBO;
import com.multithreading.bo.District;
import com.multithreading.service.BaseSiteSaveService;
import com.multithreading.service.BaseSiteSyncFailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BaseSiteSyncFailServiceImpl implements BaseSiteSyncFailService {

    private static final String DEFAULT_BASE_SITE_CODE = "UNKNOWN";
    private static final String FAILED_STATUS = "FAILED";

    @Autowired
    private BaseSiteSyncFailMapper baseSiteSyncFailMapper;

    @Autowired
    private BaseSiteSaveService baseSiteSaveService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * saveFailRecord 因为 REQUIRES_NEW 开启一个新事务，把失败基站的数据、批次号、错误信息保存到失败表
     * @param batchNo
     * @param district
     * @param baseSiteModel
     * @param exception
     */
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

    @Override
    public List<BaseSiteSyncFailEntity> findFailRecords(String batchNo) {
        return baseSiteSyncFailMapper.findByBatchNo(batchNo);
    }

    @Override
    public int retryFailRecords(String batchNo) {
        List<BaseSiteSyncFailEntity> failRecords = baseSiteSyncFailMapper.findByBatchNo(batchNo);
        int successCount = 0;

        for (BaseSiteSyncFailEntity failRecord : failRecords) {
            try {
                BaseSiteSyncBO baseSiteSyncBO = objectMapper.readValue(failRecord.getRawDataJson(), BaseSiteSyncBO.class);
                //重试机制，重新入库失败的基站信息，因为 baseSiteSaveService 有 @Transactional 事务注解，所以重试机制也是有事物的
                baseSiteSaveService.saveBaseSiteData(baseSiteSyncBO);
                baseSiteSyncFailMapper.updateRetrySuccess(failRecord.getId());
                successCount++;
            } catch (Exception e) {
                baseSiteSyncFailMapper.updateRetryFailed(failRecord.getId(), buildErrorMessage(e));
            }
        }

        return successCount;
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
