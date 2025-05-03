package com.multithreading.serviceImpl;

import com.multithreading.dao.StaticMapper;
import com.multithreading.model.BaseSiteModel;
import com.multithreading.service.baseSiteService;
import com.multithreading.utils.MsgThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 利用多线程将数据插入库中
 * 该函数的主要功能是使用多线程将基站数据批量插入数据库，具体逻辑如下：
 * 1.接收基站数据列表 list。
 * 2.使用Collections.synchronizedList，将其转为线程安全的synBaseSiteList
 * 3.获取线程池实例 MsgThreadPool。
 * 4.使用 CountDownLatch 控制主线程等待所有子线程完成。
 * 5.每个数据封装成任务提交线程池，异步执行 saveSiteDatas() 入库
 * 6.调用countDownLatch countDown()方法将计数器count-1，标记已经完成一个任务，主线程 await() 等待全部完成。
 * 7.saveSiteData() 调用 DAO 层方法批量插入各类数据。
 * ✅ 总结：利用线程池并发处理数据入库，提高效率，并保证主线程等待所有入库完成。
 */
@Service
public class baseSiteServiceImplNew implements baseSiteService {

    @Autowired
    private StaticMapper staticMapper;

    @Override
    public void baseSiteService(List<BaseSiteModel> list) {
        try {
            //使用Collections.synchronizedList，将其转为线程安全的synBaseSiteList
            List<BaseSiteModel> synBaseSiteList = Collections.synchronizedList(list);

            // 获取线程池实例 MsgThreadPool。
            ThreadPoolTaskExecutor threadPoolInstance = MsgThreadPool.getPoolInstance();

            //使用同步工具类CountDownLatch，并使用他的计数器功能，让主线程等待入库线程执行完入库任务再继续执行
            //计数器countDownLatch，数量设为数据集合的长度
            CountDownLatch countDownLatch = new CountDownLatch(synBaseSiteList.size());

            // 循环baseSiteList将数据插入库中,每个线程执行一个基站设备信息入库操作
            for (BaseSiteModel synBaseSiteModel : synBaseSiteList) {
                // 每条基站数据封装成任务提交线程池，异步执行 saveSiteDatas() 入库
                // executor 异步执行 提交的Runnable 任务 synBaseSiteModel
                /**
                 * 调用 execute(Runnable) 方法时，该方法会立即返回，不会阻塞调用线程等待任务完成，即不会阻塞当前循环等待其完成
                 * 相反，它会将任务放入线程池的任务队列中或者直接由一个空闲的工作线程执行
                 * 这种非阻塞特性使得调用方可以继续执行后续代码，无需等待当前循环任务执行完毕，实现了异步执行的效果。
                 * 即可以继续执行下一个循环,无需等待当前循环执行完毕。
                 *
                 */
                threadPoolInstance.execute(() -> {
                    try {
                        saveSiteData(synBaseSiteModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //调用countDownLatch countDown()方法将计数器countDownLatch-1，标记已经完成一个任务
                        countDownLatch.countDown();
                    }
                });
            }
            //当计数器countDownLatch不为0时，调用await()使主线程处于阻塞状态，等待数据入库的所有参与者执行结束，再执行主线程
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveSiteData(BaseSiteModel baseSiteModel) {
        staticMapper.saveAntennaBatch(baseSiteModel.getAntennaList());
        staticMapper.saveAAUBatch(baseSiteModel.getAauModelList());
        staticMapper.saveBBUBatch(baseSiteModel.getBbuModelList());
        staticMapper.saveOilDateBatch(baseSiteModel.getOilModelList());
        staticMapper.saveAirConditionDataBatch(baseSiteModel.getAirConditionList());
    }
}
