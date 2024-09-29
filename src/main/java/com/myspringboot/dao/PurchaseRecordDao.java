package com.myspringboot.dao;

import com.myspringboot.pojo.PurchaseRecordPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xiehang
 * @create 2022-08-07 17:50
 */
@Mapper
public interface PurchaseRecordDao {
    public int insertPurchaseRecord(PurchaseRecordPo pr);
}
