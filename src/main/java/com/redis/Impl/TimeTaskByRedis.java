package com.redis.Impl;

import com.redis.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xiehang
 * @date 2023/10/16 22:27
 * Redis实现分布式锁
 *
 * 我们对锁过期主要做了三层处理：
 * 第一，加锁时设置过期时间，避免死锁；
 * 第二，长任务执行期间启动续期线程，定期给自己的锁续期，避免任务没执行完锁就过期；
 *      当线程成功获取锁之后，会启动一个后台守护线程。
 *      这个线程通过一个 AtomicBoolean 开关控制生命周期，只要业务任务还没执行完，它就会按照配置的续期间隔休眠一段时间，
 *      比如每 5 秒醒一次。醒来后不会直接续期，而是执行 Lua 脚本，先判断 Redis 里锁的 value 是否还是当前线程生成的 UUID。
 *      如果是，说明锁还属于当前线程，就执行 PEXPIRE 把过期时间重新延长；如果不是，说明锁已经过期或被其他线程拿走了，续期线程就退出。
 * 第三，续期和释放锁都通过 Lua 校验锁 value，只有确认锁仍然属于当前线程才操作，防止锁过期后误续期或误删别人重新获取的锁
 *
 */
@Configurable
@Component
public class TimeTaskByRedis {

    private static final Logger logger = LoggerFactory.getLogger(TimeTaskByRedis.class);

    private static final Long LUA_SUCCESS = 1L;

    /**
     * 如果 Redis 里的锁 value 等于我自己的 UUID
     *     就把锁过期时间重新设置为 30 秒
     * 否则
     *     什么都不做
     */
    private static final String RENEW_LOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('pexpire', KEYS[1], ARGV[2]) " +
                    "else return 0 end";

    /**
     * 如果 Redis 里的锁 value 等于我自己的 UUID
     *     删除锁
     * 否则
     *     不删除
     */
    private static final String RELEASE_LOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) " +
                    "else return 0 end";

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private RedisService redisService;

    /**
     * 设置锁的初始过期时间（毫秒）
     * expirationTime：锁过期时间，比如 30000 毫秒，就是 30 秒。
     **/
    @Value("${spring.jedis.expirationTime}")
    private int expirationTime = 30000;

    /**
     * 设置锁续期间隔（毫秒）
     * RENEWAL_INTERVAL：续期间隔，比如 5000 毫秒，就是每 5 秒续一次锁。
     * 锁默认 30 秒过期，但如果任务还没执行完，就每 5 秒把锁再延长到 30 秒。
     */
    @Value("${spring.jedis.renewalTime}")
    private long RENEWAL_INTERVAL;

    // 定时任务开关名称
    private static final String SCHEDULED_TASK_SWITCH = "scheduledTaskSwitch";
    private static final String SCHEDULED_TASK_SWITCH_STATUS = "scheduledTaskSwitchStatus";

    // 定时任务锁名称
    private static final String SCHEDULED_ENV_LOCK = "ScheduledEnvLock";

    /**
     * 定时任务，清理一年前的数据
     * 清理执行记录表 t_exexution_record
     * 清理执行信息表 t_exexution_info
     * 要求：
     *  1. 获取执行定时任务的开关状态 scheduledTaskStatus
     *  2. 使用 Redis SET NX PX 原子命令尝试获取分布式锁
     *  3. 执行删除任务
     *  4. 定时任务执行完，校验锁持有者后释放锁
     */
    @Scheduled(fixedDelayString = "${spring.jedis.scheduledFixedDelay:60000}")
    public void deleteOverExes() {
        //每次任务执行前，生成一个唯一 ID。这个 ID 就是“锁的主人身份证”。
        String lockValue = UUID.randomUUID().toString();

        /**
         * 控制续期线程是否继续运行。
         * 创建一个线程安全的布尔变量 renewalRunning，初始值为 false
         * AtomicBoolean 是 java.util.concurrent.atomic 包里的原子类，可以保证读写和比较更新操作是原子的
         * 这样多个线程同时进来时，只有一个线程能成功把 false 改成 true，其他线程就知道任务已经有人在执行了。
         */
        AtomicBoolean renewalRunning = new AtomicBoolean(false);
        //保存续期线程对象，方便最后中断它
        Thread renewalThread = null;
        //记录当前线程是否真的拿到了锁
        boolean locked = false;
        try {
            String scheduledTaskSwitchStatus = null;
            if (jedisCluster != null) {
                //从 Redis 里读取定时任务开关。
                scheduledTaskSwitchStatus = jedisCluster.get(SCHEDULED_TASK_SWITCH);
            }
            /**
             * 这里有两个条件：
             * 1. 定时任务开关是 on
             * 2. 当前机器成功拿到分布式锁
             * 都满足，才执行清理任务。
             */
            if (SCHEDULED_TASK_SWITCH_STATUS.equals(scheduledTaskSwitchStatus)
                    && getNxScheduledTaskLock(SCHEDULED_ENV_LOCK, lockValue)) {
                locked = true;
                logger.info("获取 ScheduledEnvLock success, lockValue={}", lockValue);

                /**
                 * 拿到锁后启动续期线程
                 * 意思是：我已经拿到锁了，启动一个后台线程，定期帮我续期。
                 * 为什么要续期？
                 * 因为你的任务可能执行超过 30 秒。
                 * 如果不续期，锁 30 秒后过期，其他机器可能又拿到锁，导致多个机器同时执行清理任务。
                 */
                renewalRunning.set(true);
                renewalThread = startRenewalThread(SCHEDULED_ENV_LOCK, lockValue, renewalRunning);

                // 执行删除任务，任务耗时超过初始过期时间时，由续期线程延长锁有效期。
                redisService.deleteOverExes();
            }
        } catch (Exception e) {
            logger.error("delete timer error", e);
        } finally {
            renewalRunning.set(false);
            if (renewalThread != null) {
                renewalThread.interrupt();
            }
            if (locked) {
                boolean released = releaseScheduledTaskLock(SCHEDULED_ENV_LOCK, lockValue);
                logger.info("release ScheduledEnvLock result: {}", released);
            }
        }
    }

    /**
     * 使用 SET key value NX PX milliseconds 原子获取分布式锁。
     */
    private boolean getNxScheduledTaskLock(String key, String lockValue) {
        String result = null;
        try {
            if (jedisCluster != null && key != null && expirationTime > 0) {
                result = jedisCluster.set(key, lockValue, "NX", "PX", expirationTime);
                //result = jedisCluster.set(key, value, "NX", "PX", expirationTime);
                //它对应 Redis 命令：SET ScheduledEnvLock uuid NX PX 30000
                // 如果成功，Redis 返回： OK
            }
        } catch (Exception e) {
            logger.error("set lock {} failed", key, e);
        }
        //如果成功，Redis 返回： OK,这里返回 true 就表示拿到锁了。
        return "OK".equals(result);
    }

    /**
     * 启动续期线程。续期前先校验锁 value，避免给其他节点后来获取的锁续期。
     */
    private Thread startRenewalThread(String key, String value, AtomicBoolean renewalRunning) {
        Thread renewalThread = new Thread(() -> {
            while (renewalRunning.get()) {
                try {
                    Thread.sleep(RENEWAL_INTERVAL);
                    if (!renewScheduledTaskLock(key, value)) {
                        logger.warn("renew ScheduledEnvLock failed, lock may be expired or transferred");
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "scheduled-env-lock-renewal");
        renewalThread.setDaemon(true);
        renewalThread.start();
        return renewalThread;
    }

    private boolean renewScheduledTaskLock(String key, String value) {
        try {
            Object result = jedisCluster.eval(
                    RENEW_LOCK_SCRIPT,
                    Collections.singletonList(key),
                    Arrays.asList(value, String.valueOf(expirationTime)));
            return LUA_SUCCESS.equals(result);
        } catch (Exception e) {
            logger.error("renew lock {} failed", key, e);
            return false;
        }
    }

    private boolean releaseScheduledTaskLock(String key, String value) {
        try {
            Object result = jedisCluster.eval(
                    RELEASE_LOCK_SCRIPT,
                    Collections.singletonList(key),
                    Collections.singletonList(value));
            return LUA_SUCCESS.equals(result);
        } catch (Exception e) {
            logger.error("release lock {} failed", key, e);
            return false;
        }
    }
}
