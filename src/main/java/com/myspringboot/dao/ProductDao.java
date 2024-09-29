package com.myspringboot.dao;

import com.myspringboot.pojo.ProductPo;
import com.myspringboot.model.UserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xiehang
 * @create 2022-08-07 17:48
 */
@Mapper
public interface ProductDao {
    // 获取产品
    public ProductPo getProduct(Long id);

    //减库存，而@Param标明MyBatis参数传递给后台
    public int decreaseProduct(@Param("id") Long id, @Param("quantity") int quantity);

    List<UserModel> findDataList();

    void insertDataList(List<UserModel> dataList);
}
