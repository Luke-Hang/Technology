package com.myspringboot.serviceImpl;

import com.myspringboot.dao.ProductDao;
import com.myspringboot.dao.PurchaseRecordDao;
import com.myspringboot.pojo.ProductPo;
import com.myspringboot.pojo.PurchaseRecordPo;
import com.myspringboot.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xiehang
 * @create 2022-08-07 17:52
 */
@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private PurchaseRecordDao purchaseRecordDao;

    /**
     * 获取产品信息
     * @param userId 用户编号
     * @param productId 产品编号
     * @param quantity 购买数量
     * @return
     */
    @Override
    //启动spring数据库事务管理机制
    @Transactional
    public boolean purchase(Long userId, Long productId, int quantity) {

        ProductPo product = productDao.getProduct(productId);
        // 比较库存和购买数量
        if (product.getStock() < quantity) {
            // 库存不足
            return false;
        }
        // 扣减库存
        productDao.decreaseProduct(productId, quantity);
        // 初始化购买记录
        PurchaseRecordPo pr = this.initPurchaseRecord(userId, product, quantity);
        // 插入购买记录
        purchaseRecordDao.insertPurchaseRecord(pr);
        return true;
    }

    private PurchaseRecordPo initPurchaseRecord(Long userId, ProductPo product, int quantity) {
        PurchaseRecordPo pr = new PurchaseRecordPo();
        pr.setNote("购买日志，时间：" + System.currentTimeMillis());
        pr.setPrice(product.getPrice());
        pr.setProductId(product.getId());
        pr.setQuantity(quantity);
        double sum = product.getPrice() * quantity;
        pr.setSum(sum);
        pr.setUserId(userId);
        return pr;
    }

    @Override
    public boolean purchaseRedis(Long userId, Long productId, int quantity) {
        return false;
    }

    @Override
    public boolean dealRedisPurchase(List<PurchaseRecordPo> prpList) {
        return false;
    }
}
