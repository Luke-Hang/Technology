package com.myspringboot.redis.Impl;

import com.myspringboot.redis.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

/**
 * @author xiehang
 * @date 2023/10/16 22:27
 * Redis实现分布式锁
 */
@Configurable
@Component
public class TimeTaskByRedis {

    private static final Logger logger = LoggerFactory.getLogger(TimeTaskByRedis.class);

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private RedisService redisService;

    /**
     * 设置锁的初始过期时间（毫秒）
     **/
    @Value("${spring.jedis.expirationTime}")
    private int expirationTime;

    /**
     * 设置锁续期间隔（毫秒）
     */
    @Value("${spring.jedis.renewalTime}")
    private long RENEWAL_INTERVAL;   // 续期间隔（毫秒）

    //定时任务开关名称
    private static String SCHEDULED_COMMON_STATUS = "scheduledCommonStatus";

    //生成唯一的请求标识：在尝试获取锁之前，首先需要生成一个与本次请求相关的唯一标识，通常是包含客户端ID和时间戳的信息，确保这个值在分布式环境中是唯一的。
    //定时任务锁名称，也是唯一的请求标识
    private static String SCHEDULED_ENV_LOCK = "ScheduledEnvLock";



    /**
     * 定时任务，清理一年前的数据
     * 清理执行记录表 t_exexution_record
     * 清理执行信息表 t_exexution_info
     * 要求：
     *  1.获取执行定时任务的开关状态scheduledTaskStatus
     *  2.使用setnx尝试获取分布式锁，即jedisCluster.setnx有返回值，即可以set进去，拿到分布式锁
     *  3.执行删除任务
     *  4.定时任务执行完，释放锁
     */
    @Scheduled
    public void deleteOverExes() {
        try {
            //获取执行定时任务的开关状态scheduledTaskStatus
            //定时任务开关为on的时候表示开关被打开，off时表示开关关闭
            String scheduledTaskStatus = null;
            if (jedisCluster != null) {
                scheduledTaskStatus = jedisCluster.get(SCHEDULED_COMMON_STATUS);
            }
            //定时任务开关状态处于打开状态，使用setnx尝试获取分布式锁
            if ("on".equals(scheduledTaskStatus)
                    && getNxScheduledTaskLock(SCHEDULED_ENV_LOCK, "on")) {

                logger.info("获取 ScheduledEnvLock success");

                //启动续期线程
                startRenewalThread(SCHEDULED_ENV_LOCK);

                //3.执行删除任务，假设删除操作耗时40000毫秒，超过初始锁的过期时间
                redisService.deleteOverExes();
            }
        } catch (Exception e) {
            logger.info("delete timer error", e.getMessage());
        } finally {
            //4.定时任务执行完，释放锁
            Long res = jedisCluster.del(SCHEDULED_ENV_LOCK);
            logger.info("release ScheduledEnvLock success!");
        }
    }

    /**
     * 使用setnx获取分布式锁，同时设置所的过期时间
     * @param key
     * @param value
     * @return
     */
    private boolean getNxScheduledTaskLock(String key, Object value) {
        long result = 0;
        try {
            if (jedisCluster != null && key != null) {
                result = jedisCluster.setnx(key, value.toString());
                //设置过期时间，避免死锁
                if (expirationTime > 0) {
                    jedisCluster.expire(key, expirationTime);
                }
            }
        } catch (Exception e) {
            logger.error("setObject {}", key, e);
        }
        /**
         * jedisCluster.setnx(key, value.toString())尝试在Redis中设置唯一的key和value，
         *      如果key不存在的话。该命令的返回值为1表示设置成功，即获取锁。
         *      如果返回其他值表示设置失败，即未获取锁。
         */
        //如果setnx可以正确set则返回1，此时1==1，返回true
        //如果setnx不可以正确set则返回其他值，此时!=1，返回false
        return result == 1;
    }


    /**
     * 启动续期线程
     * 自动续期机制：通过后台线程定期延长锁的过期时间，确保锁不会提前释放。
     * @param key
     */
    private void startRenewalThread(String key) {
        Thread renewalThread = new Thread(() -> {
            while (true) {
                try {
                    // 检查锁是否仍然存在
                    if (SCHEDULED_ENV_LOCK.equals(jedisCluster.get(key))) {
                        // 设置延长锁的过期时间，延长过期时间360000/1000=36毫秒
                        jedisCluster.expire(key, expirationTime / 1000);
                        //通过 Thread.sleep 等待一段时间后再次续期，确保分布式锁在任务执行期间不会过期。
                        Thread.sleep(RENEWAL_INTERVAL); // 等待一段时间5秒再续期
                    } else {
                        break; // 锁已被其他线程抢占
                    }
                } catch (InterruptedException e) {
                    break; // 中断续期线程
                }
            }
        });
        renewalThread.start();
    }
}
