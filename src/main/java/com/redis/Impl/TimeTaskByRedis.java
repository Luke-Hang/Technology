package com.redis.Impl;

import com.redis.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * 学习示例：通过 Redis 分布式锁，避免多个实例同时清理一年前的数据。
 * 删除条件及分批删除逻辑由 RedisService 负责，这里只演示任务调度和锁管理。
 */
@Component
public class TimeTaskByRedis {

    private static final Logger logger = LoggerFactory.getLogger(TimeTaskByRedis.class);
    private static final String LOCK_KEY = "ScheduledEnvLock";
    // 保留原有开关约定：Redis 中的值等于此字符串才执行任务。
    private static final String SWITCH_ENABLED = "scheduledTaskSwitchStatus";

    // 校验 UUID 和操作锁必须在同一段 Lua 中完成，避免操作到其他实例的锁。
//    if redis.call('get', KEYS[1]) == ARGV[1] then
//          return redis.call('del', KEYS[1])
//    else
//          return 0
//    end
    private static final String RENEW_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('pexpire', KEYS[1], ARGV[2]) else return 0 end";
    private static final String RELEASE_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) else return 0 end";

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private RedisService redisService;

    @Value("${spring.jedis.expirationTime:30000}")
    private int expirationTime;

    @Value("${spring.jedis.renewalTime:5000}")
    private long renewalInterval;

    /** 默认在上次执行结束 60 秒后再次触发，便于学习观察。 */
    @Scheduled(fixedDelayString = "${spring.jedis.scheduledFixedDelay:60000}")
    public void deleteOverExes() {
        try {
            String lockValue = UUID.randomUUID().toString();
            // NX：锁不存在才设置；PX：设置毫秒级过期时间，避免实例宕机后锁一直存在。
//            参数	            含义	                    示例
//            LOCK_KEY	        Redis 锁的 Key	            "order:lock"
//            lockValue	        锁的唯一标识，用于识别持有者	"uuid123"
//            "NX"	            只有 Key 不存在时才执行 SET	不允许覆盖已有锁        NX Not eXists 的缩写,不存在才设
//            "PX"	            设置过期时间，单位为毫秒	     毫秒
//            expirationTime	锁的过期时长	                30000（30 秒）

            /**
             * 面试回答：
             *      使用 Redis 的 SET NX PX 命令获取分布式锁，
             *      NX 保证锁不存在时才能获取成功，PX 设置锁的过期时间。
             *      如果加锁成功返回 OK，锁已存在时通常返回 null
             *      代码通过判断返回值是否为 OK 来确定是否获取到锁，如果失败就直接返回，不执行业务。”
             */
            String keyStatus = jedisCluster.set(LOCK_KEY, lockValue, "NX", "PX", expirationTime);
            if (!"OK".equals(keyStatus)) {
                return;
            }

            Thread renewalThread = null;
            try {
                renewalThread = startRenewalThread(lockValue);
                logger.info("开始清理一年前的数据");
                redisService.deleteOverExes();
                logger.info("清理任务执行完成");
            } finally {
                if (renewalThread != null) {
                    renewalThread.interrupt();
                }
                jedisCluster.eval(RELEASE_SCRIPT, Collections.singletonList(LOCK_KEY),
                        Collections.singletonList(lockValue));
            }
        } catch (Exception e) {
            logger.error("清理任务或锁操作失败", e);
        }
    }

    /** 长任务执行期间定期续期；任务结束后通过 interrupt 停止，无需额外的布尔开关。 */
    private Thread startRenewalThread(String lockValue) {
        Thread thread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(renewalInterval);
                    Object result = jedisCluster.eval(RENEW_SCRIPT,
                            Collections.singletonList(LOCK_KEY),
                            Arrays.asList(lockValue, String.valueOf(expirationTime)));
                    if (!Long.valueOf(1L).equals(result)) {
                        logger.warn("锁已失效，停止续期；正在执行的清理任务不会自动停止");
                        return;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logger.error("锁续期失败，正在执行的清理任务不会自动停止", e);
            }
        }, "scheduled-env-lock-renewal");
        thread.setDaemon(true);
        thread.start();
        return thread;
    }
}
