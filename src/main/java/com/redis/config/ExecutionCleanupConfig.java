package com.redis.config;

import com.redis.Impl.RedisServiceImpl;
import com.redis.Impl.TimeTaskByRedission;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

/** 显式导入所需组件，避免为示例扫描整个 com.redis 包并启动 Jedis 任务。 */
@Configuration
@EnableScheduling
@Import({TimeTaskByRedission.class, RedisServiceImpl.class})
@ConditionalOnProperty(prefix = "cleanup.execution", name = "enabled", havingValue = "true")
public class ExecutionCleanupConfig {

    /** 单例客户端由 Spring 创建，应用关闭时统一关闭，任务中只负责使用。 */
    /**
     * 看门狗的运行过程是：
     *  1. 获取锁成功：给锁设置有效期，当前配置为 30 秒。
     *  2. 后台定期续期：通常每隔有效期的三分之一，也就是约 10 秒，检查锁是否仍属于当前持有者。
     *  3. 确认归属后延长有效期：通过 Lua 原子执行校验和续期，将剩余有效期重新设置为 30 秒。
     *  4. 完全解锁后停止续期：业务完成并释放锁，对应的续期任务取消。
     *  5. 实例宕机后自动过期：续期停止，锁在剩余有效期结束后过期。
     */

    @Value("${spring.jedis.cluster.nodes}")
    private String nodes;

    @Value("${spring.jedis.password:}")
    private String password;

    @Value("${cleanup.execution.watchdog-timeout:30000}")
    private long watchdogTimeout;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        if (watchdogTimeout <= 0) {
            throw new IllegalArgumentException("看门狗超时时间必须大于 0");
        }
        String[] addresses = Arrays.stream(nodes.split(","))
                .map(String::trim)
                .filter(node -> !node.isEmpty())
                .map(node -> node.contains("://") ? node : "redis://" + node)
                .toArray(String[]::new);
        if (addresses.length == 0) {
            throw new IllegalArgumentException("Redis 集群节点不能为空");
        }
        Config config = new Config();
        config.setLockWatchdogTimeout(watchdogTimeout);// 设置看门狗时间
        ClusterServersConfig cluster = config.useClusterServers().addNodeAddress(addresses);
        if (!password.isEmpty()) {
            cluster.setPassword(password);
        }
        return Redisson.create(config);
    }
}
