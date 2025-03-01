package com.myspringboot.pojo;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author xiehang
 * @create 2022-08-07 17:22
 */
@Setter
@Getter
@Alias("purchaseRecord")
public class PurchaseRecordPo implements Serializable {
    private static final long serialVersionUID = -4749810879756279199L;

    private Long id;
    private Long userId;
    private Long productId;
    private double price;
    private int quantity;
    private double sum;
    private Timestamp purchaseTime;
    private String note;
}
