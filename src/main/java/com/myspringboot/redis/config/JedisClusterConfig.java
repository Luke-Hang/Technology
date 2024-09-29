package com.myspringboot.redis.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xiehang
 * @date 2023/10/16 22:13
 */
public class JedisClusterConfig {
    @Inject
    private RedisProperties redisProperties;

    @Bean
    @Singleton
    public JedisCluster getJedisCluster() {
//        String[] serverArray = redisProperties.getClusterNodes().split(",");
//        Set<HostAndPort> nodes = new HashSet<>();
//        for (String ipPort: serverArray) {
//            String[] ipPortPair = ipPort.split(":");
//            nodes.add(new HostAndPort(ipPortPair[0].trim(),Integer.valueOf(ipPortPair[1].trim())));
//        }
//        return new JedisCluster(nodes, redisProperties.getCommandTimeout());
        return null;
    }
}
