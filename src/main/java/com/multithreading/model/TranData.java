package com.multithreading.model;

import com.multithreading.dao.StaticMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author xiehang
 * @date 2023/3/31 14:41
 */
@Data
public final class TranData implements Runnable {

    @Autowired
    private StaticMapper staticMapper;

    private BaseSiteModel baseSiteModel;
    private List<BaseSiteModel> listMap;
    private CountDownLatch countDownLatch;

    // 提供构造方法
    public TranData(BaseSiteModel baseSiteModel, List<BaseSiteModel> listMap,
                    CountDownLatch countDownLatch) {
        this.baseSiteModel = baseSiteModel;
        this.listMap = listMap;
        this.countDownLatch = countDownLatch;
    }

    //无参构造
    public TranData() {

    }

    // 数据插入数据库多线程操作任务
    @Override
    public void run() {
        try {
            saveSiteDatas(baseSiteModel, listMap, countDownLatch);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            //调用countDownLatch countDown()方法将计数器countDownLatch-1，标记已经完成一个任务
            countDownLatch.countDown();
        }
    }

    /**
     * @param baseSiteModel2
     * @param listMap2
     * @param countDownLatch2
     */
    private void saveSiteDatas(BaseSiteModel baseSiteModel2, List<BaseSiteModel> listMap2,
                               CountDownLatch countDownLatch2) {
        staticMapper.saveDta(listMap2);
    }
}
