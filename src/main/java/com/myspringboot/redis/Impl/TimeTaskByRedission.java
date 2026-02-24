package com.myspringboot.redis.Impl;

import com.myspringboot.redis.service.RedisService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author xiehang
 * @date 2025/3/1 14:21
 */
@Component
public class TimeTaskByRedission {

    @Autowired
    private RedisService redisService;

    private static String SCHEDULED_ENV_LOCK = "ScheduledEnvLock";

    public void deleteOverExes() throws InterruptedException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);

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
            redisson.shutdown();
        }
    }

}
