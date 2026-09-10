package com.multithreading.serviceImpl;

import com.multithreading.bo.BaseSiteSyncBO;
import com.multithreading.bo.District;
import com.multithreading.service.BaseSiteSaveService;
import com.multithreading.service.BaseSiteService;
import com.multithreading.service.BaseSiteSyncFailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * 利用多线程将数据插入库中
 * 该函数的主要功能是使用多线程将基站数据批量插入数据库，具体逻辑如下：
 * 1.接收基站数据列表 list。
 * 2.使用Collections.synchronizedList，将其转为线程安全的synBaseSiteList
 * 3.获取线程池实例 MsgThreadPool。
 * 4.使用 CountDownLatch 控制主线程等待所有子线程完成。
 * 5.每个数据封装成任务提交线程池，异步执行 saveBaseSiteData() 入库
 * 6.调用countDownLatch countDown()方法将计数器count-1，标记已经完成一个任务，主线程 await() 等待全部完成。
 * 7.saveSiteData() 调用 DAO 层方法批量插入各类数据。
 * ✅ 总结：利用线程池并发处理数据入库，提高效率，并保证主线程等待所有入库完成。
 */
@Service
@Primary
public class BaseSiteServiceImpl implements BaseSiteService {

    @Autowired
    private BaseSiteSaveService baseSiteSaveService;

    @Autowired
    private BaseSiteSyncFailService baseSiteSyncFailService;

    @Resource(name = "dataSyncThreadPool")
    private ThreadPoolTaskExecutor dataSyncThreadPool;

    /**
     *
     * @param baseSiteModelList 某个行政区域下的所有基站信息
     */
    @Override
    public void baseSiteService(District district, List<BaseSiteSyncBO> baseSiteModelList) {
        if (baseSiteModelList == null || baseSiteModelList.isEmpty()) {
            return;
        }

        //把传进来的 baseSiteModelList 拷贝成了一份新的 ArrayList 快照。
        // 主线程：遍历 baseSiteList，提交任务
        // 子线程：处理单个 BaseSiteModel
        // 子线程并没有去修改或遍历 baseSiteList，所以 baseSiteList 这个容器本身没有多线程读写冲突
        // new ArrayList<>(baseSiteModelList) 保证的是：
        //      当前这次同步要处理多少个基站是固定的
        //      不会因为外部修改原始 baseSiteModelList，影响本次 for 循环和 CountDownLatch 数量
        // 所以当前线程安全依赖的是：
        //      1. baseSiteList 用 new ArrayList 做快照，避免原 list 数量变化
        //      2. 子线程只处理各自拿到的 BaseSiteModel，不共享修改 baseSiteList
        //      3. CountDownLatch 是 JDK 并发工具，本身线程安全
        //      4. baseSiteSaveService 是 Spring Bean，通常无状态，只调用 Mapper，不保存共享可变成员变量
        // 线程安全说明：
            // 1. 主线程：遍历 baseSiteList 并提交任务，子线程不会修改 baseSiteList。
            // 2. baseSiteList 是由入参拷贝出来的快照，可以避免外部修改原始 List 影响本次同步。
                    //baseSiteModelList：外部传进来的原始 List
                    //baseSiteList：当前方法内部新建的 List
                    //它们是两个不同的 List 对象，即使外部后面修改原始 List，当前方法内部的 baseSiteList 还是不变的
            // 3. 子线程只处理自己拿到的 BaseSiteModel，并通过 CountDownLatch 通知主线程任务完成。
            // 4. CountDownLatch 可以被多个子线程安全调用 countDown()。
        // 因此，在 BaseSiteModel 及其内部设备 list 不被其他线程并发修改的前提下，这里没有 list 层面的线程安全问题。
        List<BaseSiteSyncBO> baseSiteList = new ArrayList<>(baseSiteModelList);
        String batchNo = UUID.randomUUID().toString();

        //使用同步工具类CountDownLatch，并使用他的计数器功能，让主线程等待入库线程执行完入库任务再继续执行
        //计数器countDownLatch，数量设为数据集合的长度
        final CountDownLatch countDownLatch = new CountDownLatch(baseSiteList.size());
        final Queue<Exception> failures = new ConcurrentLinkedQueue<>();

        try {
            // 提交任务
            // 循环 baseSiteList 将数据插入库中,每个线程执行一个基站设备信息入库操作
            for (BaseSiteSyncBO synBaseSiteModel : baseSiteList) {
                // 每条基站数据封装成任务提交线程池，异步执行 saveSiteDatas() 入库
                // executor 异步执行 提交的Runnable 任务 synBaseSiteModel
                /**
                 * 调用 execute(Runnable) 方法时，该方法会立即返回，不会阻塞调用线程等待任务完成，即不会阻塞当前循环等待其完成
                 * 相反，它会将任务放入线程池的任务队列中或者直接由一个空闲的工作线程执行
                 * 这种非阻塞特性使得调用方可以继续执行后续代码，无需等待当前循环任务执行完毕，实现了异步执行的效果。
                 * 即可以继续执行下一个循环,无需等待当前循环执行完毕。
                 *
                 */


                /**
                 * 某个基站入库
                 *   -> 调用 baseSiteSaveService.saveBaseSiteData(...)
                 *   -> 这个方法在 BaseSiteSaveServiceImpl 的事务里执行
                 *   -> 中途异常
                 *   -> 当前这个基站的入库事务回滚
                 *   -> 异常被子线程里的 catch 捕获
                 *   -> 调用 handleSaveFailure(...)
                 *   -> handleSaveFailure 再调用 baseSiteSyncFailService.saveFailRecord(...)
                 *   -> saveFailRecord 因为 REQUIRES_NEW 开启一个新事务
                 *   -> 把失败基站的数据、批次号、错误信息保存到失败表
                 *   -> 最后 failures.add(exception)
                 *
                 *
                 *
                 基站 1：成功 -> 正常提交
                 基站 2：失败 -> 业务数据回滚 -> 失败表保存
                 基站 3：成功 -> 正常提交
                 基站 4：失败 -> 业务数据回滚 -> 失败表保存

                 一个基站成功，只提交这个基站的数据；
                 一个基站失败，只回滚这个基站的数据；
                 失败记录单独用 REQUIRES_NEW 保存，不跟业务入库事务一起回滚；
                 其他基站不受它影响，继续正常执行。
                 *
                 */
                dataSyncThreadPool.execute(() -> {
                    try {
                        //成功的基站：正常提交，失败基站：本次入库全部回滚
                        baseSiteSaveService.saveBaseSiteData(synBaseSiteModel);
                    } catch (Exception e) {
                        //处理失败基站、异常被子线程里的 catch 捕获
                        handleSaveFailure(batchNo, district, synBaseSiteModel, e, failures);
                    } finally {
                        //调用countDownLatch countDown()方法将计数器countDownLatch-1，标记已经完成一个任务
                        //某个基站入库失败：它自己的事务会回滚
                        //countDownLatch.countDown() 仍然会执行，不会导致主线程一直等；
                        countDownLatch.countDown();
                    }
                });
            }
            //当计数器countDownLatch不为0时，调用await()使主线程处于阻塞状态，等待数据入库的所有参与者执行结束，再执行主线程
            //如果某个任务卡死，比如数据库连接一直阻塞，主线程会一直等。生产里更稳的是加超时时间：
            // 最多等待 40 分钟，等待所有基站入库任务执行完成。
            // 任务全部完成会提前返回 true；超过 40 分钟仍未完成则返回 false。
            boolean finished = countDownLatch.await(40, TimeUnit.MINUTES);
            if (!finished) {
                throw new RuntimeException("基站数据同步超时");
            }
            //调用方可以感知“这批基站同步不是完全成功”。
            //所有任务结束后，主线程能知道有失败，并向上抛异常
            if (!failures.isEmpty()) {
                throw new RuntimeException("Base site sync failed, batchNo: " + batchNo + ", failure count: " + failures.size(), failures.peek());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("基站数据同步被中断", e);
        }
    }

    /**
     *
     * @param batchNo 批处理编号
     * @param district
     * @param baseSiteModel
     * @param exception
     * @param failures
     */
    private void handleSaveFailure(String batchNo, District district, BaseSiteSyncBO baseSiteModel,
                                   Exception exception, Queue<Exception> failures) {
        try {
            baseSiteSyncFailService.saveFailRecord(batchNo, district, baseSiteModel, exception);
        } catch (Exception recordException) {
            exception.addSuppressed(recordException);
        }
        failures.add(exception);
    }
}
