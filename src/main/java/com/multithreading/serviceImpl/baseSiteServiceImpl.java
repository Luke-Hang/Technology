package com.multithreading.serviceImpl;

import com.multithreading.dao.StaticMapper;
import com.multithreading.model.BaseSiteModel;
import com.multithreading.service.baseSiteService;
import com.multithreading.utils.MsgThreadPool;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 利用多线程将数据插入库中
 */
@Service
public class baseSiteServiceImpl implements baseSiteService {

    @Autowired
    private StaticMapper staticMapper;

    @Override
    public void baseSiteService(List<BaseSiteModel> list) {
        try {
            //将list放入线程安全容器 Collections中，保证线程安全
            // 将集合设置为线程安全的
            List<BaseSiteModel> baseSiteList = Collections.synchronizedList(list);
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

            // 获取线程池实例
            ThreadPoolTaskExecutor executor = MsgThreadPool.getPoolInstanc();

            //使用同步工具类CountDownLatch，并使用他的计数器功能，让主线程等待入库线程执行完入库任务再继续执行
            //计数器countDownLatch，数量设为数据集合的长度
            CountDownLatch countDownLatch = new CountDownLatch(baseSiteList.size());

            // 循环baseSiteList将数据插入库中
            for (BaseSiteModel baseSiteModel : baseSiteList) {
                //组装任务
                TranData TranData= new TranData();
                //入库对象
                TranData.setBaseSiteModel(baseSiteModel);
                //入库数据
                TranData.setListMap(baseSiteList);
                //计数器
                TranData.setCountDownLatch(countDownLatch);

                //执行任务：executor.execute(TranData)线程池实例executor执行提交过来的任务TranData
                executor.execute(TranData);
//                executor.execute(new TranData(baseSiteModel, baseSiteList, countDownLatch));
            }
            //当计数器countDownLatch不为0时，调用await()使主线程处于阻塞状态，等待数据入库的所有参与者执行结束，再执行主线程
            countDownLatch.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //将数据插入数据库操作做成一个多线程任务
    @Data
    private final class TranData implements Runnable {

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
}
