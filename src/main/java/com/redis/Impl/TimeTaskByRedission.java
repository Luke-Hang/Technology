package com.redis.Impl;

import com.redis.service.RedisService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/** 清理一年前数据的定时任务；删除条件和分批处理由业务服务负责。 */


/**
 *  我们项目是一个内部部署平台，每次部署会产生部署记录和日志，需要定期清理一年前的数据。
 *  使用 Spring 的定时任务，每天凌晨 2 点触发。
 *  考虑到平台部署了多个实例，各实例都会触发任务，所以通过 Redisson 分布式锁，避免多个实例同时清理，增加数据库压力和锁竞争
 *  锁主要是避免并发执行
 *  所有实例使用相同的锁名，调用 tryLock(0, TimeUnit.SECONDS)，不等待锁，失败直接跳过，成功才执行清理。
 *  考虑到清理耗时不固定，没有指定租期，而是使用 Redisson 看门狗自动续期机制，避免任务没执行完锁就过期。
 *  清理耗时会随数据量变化，因此没有设置固定租期，而是使用 Redisson 看门狗自动续期。
 *  最后在 finally 中确认当前线程仍持有锁，再释放锁。
 */

@Component
@ConditionalOnProperty(prefix = "cleanup.execution", name = "enabled", havingValue = "true")
public class TimeTaskByRedission {

    private static final Logger logger = LoggerFactory.getLogger(TimeTaskByRedission.class);
    // 与 Jedis 示例隔离：两种锁的数据结构不同，不能共用同一个 key。
    private static final String LOCK_KEY = "technology:cleanup:execution:redisson:lock";

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisService redisService;

    /** 默认每天北京时间凌晨 2 点执行；所有实例使用相同的锁名。 */
    @Scheduled(cron = "${cleanup.execution.cron:0 0 2 * * ?}", zone = "${cleanup.execution.zone:Asia/Shanghai}")
    public void deleteOverExes() {
        /**
         * 通过 Redisson 提供的 redissonClient 获取一个 RLock 对象，后面用它加锁和解锁
         * 这一步还没有拿到锁。 可以理解为先确定“大家要争抢哪一把锁”。
         * 多个服务实例必须连接同一个 Redis 集群，并使用同样的 LOCK_KEY，才能竞争同一把锁。
         * 没有指定租期，因此启用看门狗。 0 表示不等待锁。
         * 考虑到清理耗时不固定，我们没有指定租期，使用看门狗自动续期
         * 如果使用 lock.tryLock(0, 30, TimeUnit.SECONDS); 变成固定 30 秒租期，不会使用看门狗自动续期
         */
        RLock rLock = redissonClient.getLock(LOCK_KEY);
//        locked 记录当前这次执行有没有成功拿到锁，初始为 false，最后决定是否需要释放。
        boolean locked = false;
//        System.nanoTime() 用于计算耗时。它不是当前日期时间，后面会用“结束值减开始值”得到执行时长。
        long startedAt = System.nanoTime();
        try {
            //rLock.tryLock(0, TimeUnit.SECONDS); 尝试获取锁，拿到锁返回 true ，没拿到锁 返回 false
            //0 表示等待锁的时间为零，单位是秒。立即尝试获取锁，成功返回 true，失败返回 false，不排队等待。
            //清理任务不需要每个实例都执行，所以拿不到就跳过。
            //  rLock.tryLock(0, 30, TimeUnit.SECONDS);指定租期,显式指定租期后，不会启用看门狗自动续期
            //  rLock.tryLock(0, TimeUnit.SECONDS); 不指定续期，Redisson 会通过看门狗机制自动续期，不需要自己启动续期线程。
            locked = rLock.tryLock(0, TimeUnit.SECONDS);
            if (!locked) {
                //没拿到锁，跳过任务
                logger.info("其他实例正在清理数据，跳过本次任务");
                return;
            }
            logger.info("开始清理一年前的执行记录和执行信息");
            redisService.deleteOverExes();
            logger.info("清理任务完成，耗时 {} ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("清理任务等待锁时被中断", e);
        } catch (Exception e) {
            logger.error("清理任务执行失败", e);
        } finally {
            //任务正常完成或发生异常，都进入 finally，尝试释放锁
            //进程被强制终止或宕机时，不能依赖 finally，需要靠锁过期释放。
            if (locked) {
                releaseLock(rLock);
            }
        }
    }

    /**
     *
     * 释放锁
     */
    private void releaseLock(RLock lock) {
        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            } else {
                logger.warn("清理任务结束时已失去锁，请检查续期及任务重复执行情况");
            }
        } catch (Exception e) {
            // 即使检查时还持有锁，解锁前也可能因故障失去锁。
            logger.error("释放清理任务锁失败", e);
        }
    }
}
