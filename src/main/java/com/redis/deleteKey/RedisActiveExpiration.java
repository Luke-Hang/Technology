package com.redis.deleteKey;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author xiehang
 * @date 2025/7/25 9:51
 * 定期删除
 * 定期随机抽样检查并删除过期 Key,控制检查的数量和时间，避免影响性能
 * 优点：减少内存浪费
 * 缺点：需要合理配置，避免过度消耗 CPU
 */
public class RedisActiveExpiration {
    public static void main(String[] args) throws InterruptedException {
        RedisActiveExpiration redis = new RedisActiveExpiration();

        // 设置多个有过期时间的键
        for (int i = 0; i < 30; i++) {
            redis.setKey("key" + i, "value" + i, 1000 + random.nextInt(4000));
        }

        // 让程序运行一段时间，观察定期删除的输出
        Thread.sleep(10000);
        redis.shutdown();
    }

    private Map<String, Object> dataStore = new HashMap<>();
    private Map<String, Long> expireTimes = new HashMap<>();
    private static Random random = new Random();
    // 定时任务执行器
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 启动定期删除任务，每秒执行一次
    public RedisActiveExpiration() {
        scheduler.scheduleAtFixedRate(this::activeExpireCycle, 1, 1, TimeUnit.SECONDS);
    }

    // 设置键值对，带有过期时间(毫秒)
    public void setKey(String key, Object value, long expireMillis) {
        dataStore.put(key, value);
        if (expireMillis > 0) {
            expireTimes.put(key, System.currentTimeMillis() + expireMillis);
        }
    }

    // 获取键值
    public Object get(String key) {
        return dataStore.get(key);
    }

    // 定期删除过期键, Redis 主动随机检查并删除过期 Key
    private void activeExpireCycle() {
        System.out.println("执行定期删除检查...");

        // 模拟Redis的随机采样检查
        int count = Math.min(expireTimes.size(), 20); // 每次最多检查20个键

        List<String> keysToCheck = new ArrayList<>(expireTimes.keySet());
        Collections.shuffle(keysToCheck);

        int expiredCount = 0;
        for (int i = 0; i < Math.min(count, keysToCheck.size()); i++) {
            String key = keysToCheck.get(i);
            if (System.currentTimeMillis() > expireTimes.getOrDefault(key, 0L)) {
                // 键已过期，删除
                dataStore.remove(key);
                expireTimes.remove(key);
                expiredCount++;
                System.out.println("删除过期键: " + key);
            }
        }

        System.out.println("本次检查完成，共检查" + count + "个键，删除" + expiredCount + "个过期键");
    }

    public void shutdown() {
        scheduler.shutdown();
    }


}
