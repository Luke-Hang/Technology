package com.myspringboot.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * @author xiehang
 * @create 2022-08-07 17:21
 */
@Data
//mybatis 别名定义
@Alias("product")
public class ProductPo implements Serializable {
    private static final long serialVersionUID = 8654239413428663367L;

    private Long id;
    private String productName;
    private int stock;
    private double price;
    private int version;
    private String note;
}
