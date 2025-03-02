package com.myspringboot.redis.Impl;

import com.myspringboot.redis.service.RedisService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author xiehang
 * @date 2025/3/1 14:21
 */
@Component
public class TimeTaskByRedission {

    @Value("${spring.jedis.cluster.nodes}")
    private String clusterNodes;

    @Autowired
    private RedisService redisService;

    private static String SCHEDULED_ENV_LOCK = "ScheduledEnvLock";

    /**
     * Redisson 分布式锁的核心原理
     *  1.加锁过程
     *      使用 Redis 的 SETNX 命令实现加锁操作，并设置锁的过期时间防止死锁
     *          SETNX 命令如果键key不存在的话，该命令的返回值为1表示设置成功，即获取锁，确保只有一个客户端能成功加锁
     *  2.自动续期机制，
     *      当线程成功获取锁后，Redisson 会启动一个后台任务，定期检查锁的状态
     *      并通过调用 Redis 的 EXPIRE 命令延长锁的过期时间
     *      当锁被释放或任务完成时，Redisson 会停止续期任务
     */
    public void deleteOverExes() throws InterruptedException {
        //配置 Redisson 客户端
        Config config = new Config();
        config.useClusterServers().addNodeAddress(clusterNodes);
        RedissonClient redisson = Redisson.create(config);

        //获取分布式锁
        RLock lock = redisson.getLock(SCHEDULED_ENV_LOCK);

        // 尝试加锁，最多等待 10 秒，锁的有效期为 30 秒
        boolean isLock = lock.tryLock(10, 30, TimeUnit.SECONDS);
        try {
            if (isLock) {
                System.out.println("成功获取锁！");
                try {
                    //执行删除任务，假设删除操作耗时40000毫秒，超过初始锁的过期时间
                    redisService.deleteOverExes();
                } finally {
                    lock.unlock();// 释放锁
                    System.out.println("成功释放锁！");
                }
            } else {
                System.out.println("未能获取锁！");
            }
        } finally {
            // 关闭 Redisson 客户端
            redisson.shutdown();
        }
    }
}
