package com.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * @author xiehang
 * @date 2023/10/16 22:13
 */
@Configuration
public class JedisClusterConfig {

    @Value("${spring.jedis.cluster.nodes}")
    private String clusterNodes;

    @Bean
    public JedisCluster getJedisCluster() {
        String[] hosts = clusterNodes.split(",");
        Set<HostAndPort> nodeList = new HashSet<>();
        for (String ipPort : hosts) {
            String[] ipPortPair = ipPort.split(":");
            nodeList.add(new HostAndPort(ipPortPair[0].trim(), Integer.parseInt(ipPortPair[1].trim())));
        }
        return new JedisCluster(nodeList);
    }
}
