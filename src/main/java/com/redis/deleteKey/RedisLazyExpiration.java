package com.redis.deleteKey;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 惰性删除
 *
 * 在访问 Key 时检查是否过期,过期则立即删除
 * 优点：对 CPU 友好，只在必要时处理
 * 缺点：可能导致内存浪费，如果过期 Key 不被访问就不会释放
 */
public class RedisLazyExpiration {
    public static void main(String[] args) throws InterruptedException {

        RedisLazyExpiration redis = new RedisLazyExpiration();

        // 设置一个3秒后过期的键
        redis.setKey("tempKey", "tempValue", 3000);

        // 立即获取(应该存在)
        System.out.println("立即获取: " + redis.getKey("tempKey"));

        // 等待4秒后获取(应该被惰性删除)
        Thread.sleep(4000);
        System.out.println("4秒后获取: " + redis.getKey("tempKey"));
    }


    private Map<String, Object> dataStore = new HashMap<>();
    private Map<String, Long> expireTimes = new HashMap<>();

    /**
     * 设置键值对，带有过期时间(毫秒)
     */
    public void setKey(String key, Object value, long expireMillis) {
        dataStore.put(key, value);
        if (expireMillis > 0) {
            expireTimes.put(key, System.currentTimeMillis() + expireMillis);
        }
    }

    //当客户端尝试访问一个 Key 时，Redis 会检查该 Key 是否已过期，如果过期则立即删除
    // 获取键值，实现惰性删除
    public Object getKey(String key) {
        // 检查是否过期
        if (isExpired(key)) {
            // 惰性删除
            dataStore.remove(key);
            expireTimes.remove(key);
            return null;
        }
        return dataStore.get(key);
    }

    // 检查键是否过期
    private boolean isExpired(String key) {
        Long expireTime = expireTimes.get(key);
        if (expireTime == null) {
            return false; // 没有设置过期时间
        }
        return System.currentTimeMillis() > expireTime;
    }
}
