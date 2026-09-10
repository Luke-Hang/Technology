package com.multithreading.serviceImpl;

import com.multithreading.dao.BaseSiteSaveMapper;
import com.multithreading.bo.BaseSiteSyncBO;
import com.multithreading.bo.District;
import com.multithreading.service.BaseSiteService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 利用多线程将数据插入库中
 * <p>
 * 该函数的主要功能是使用多线程将基站数据批量插入数据库，具体逻辑如下：
 * 1.接收一个包含基站数据的列表 list。
 * 2.将列表转为线程安全的 synchronizedList。
 * 3.获取线程池实例 MsgThreadPool，用于管理线程执行任务。
 * 4.使用 CountDownLatch 控制主线程等待所有子线程执行完毕。
 * 5.遍历数据列表，每个对象封装成 TranData 任务提交给线程池异步执行入库操作。
 * 6.每个线程执行完后调用 countDown()，主线程调用 await() 等待全部完成。
 * 7.实际入库方法：saveSiteDatas() 调用 DAO 层批量插入不同类型的数据。
 * ✅ 总结：利用线程池并发处理数据入库，提高效率，并保证主线程等待所有入库完成。
 */
@Service
public class BaseSiteServiceImplOld implements BaseSiteService {

    @Autowired
    private BaseSiteSaveMapper baseSiteSaveMapper;

    @Resource(name = "msgThreadPool")
    private ThreadPoolTaskExecutor msgThreadPool;

    @Override
    public void baseSiteService(District district, List<BaseSiteSyncBO> list) {
        try {
            //将list放入线程安全容器 Collections中，保证线程安全
            // 将集合设置为线程安全的
            List<BaseSiteSyncBO> baseSiteList = Collections.synchronizedList(list);
            // 将数据插入数据库操作做成一个多线程任务提交给线程池
            //也就是线程(executor)驱动任务(TranData) 通过实现Runnable来定义任务，最终由run方法执行任务
            /*
             * 线程驱动任务
             * 1.定义任务，通过实现Runnable来定义任务，如TranData实现Runnable定义了任务
             *	 然后由TranData中的run方法来执行具体的任务
             * 2.创建线程池获取线程对象，如创建ChangeMsgThreadPool，通过getPoolInstanc方法获取线程对象
             * 	这样就可以通过线程对象executor来驱动任务TranData
             * 	public void execute(Runnable task) execute需要传入一个Runnable类型的任务task
             * */


            //使用同步工具类CountDownLatch，并使用他的计数器功能，让主线程等待入库线程执行完入库任务再继续执行
            //计数器countDownLatch，数量设为数据集合的长度
            CountDownLatch countDownLatch = new CountDownLatch(baseSiteList.size());

            // 循环baseSiteList将数据插入库中,每个线程执行一个基站设备信息入库操作
            for (BaseSiteSyncBO baseSiteModel : baseSiteList) {
                //组装任务
                TaskData taskData = new TaskData();
                //入库对象
                taskData.setBaseSiteModel(baseSiteModel);
                //计数器
                taskData.setCountDownLatch(countDownLatch);
                // executor 异步执行 提交的Runnable 任务 TranData
                msgThreadPool.execute(taskData);
            }
            //当计数器countDownLatch不为0时，调用await()使主线程处于阻塞状态，等待数据入库的所有参与者执行结束，再执行主线程
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //将数据插入数据库操作做成一个多线程任务
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private final class TaskData implements Runnable {

        private BaseSiteSyncBO baseSiteModel;
        private CountDownLatch countDownLatch;


        //无参构造
        // 数据插入数据库多线程操作任务
        @Override
        public void run() {
            try {
                saveSiteDatas(baseSiteModel);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //调用countDownLatch countDown()方法将计数器countDownLatch-1，标记已经完成一个任务
                countDownLatch.countDown();
            }
        }

        /**
         * @param baseSiteModel
         */
        private void saveSiteDatas(BaseSiteSyncBO baseSiteModel) {
/*            staticMapper.saveAntennaBatch(baseSiteModel.getAntennaList());
            staticMapper.saveAAUBatch(baseSiteModel.getAauModelList());
            staticMapper.saveBBUBatch(baseSiteModel.getBbuModelList());
            staticMapper.saveOilDateBatch(baseSiteModel.getOilModelList());
            staticMapper.saveAirConditionDataBatch(baseSiteModel.getAirConditionList());*/
        }
    }
}
