package com.Ribbon;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author xiehang
 * @date 2025/1/7 21:39
 *
 * 配置 RestTemplate 被 Ribbon 代理
 */
@Component
public class Config {
    //引入ribbon的依赖,通过@LoadBalanced标记RestTemplate
    @Bean
    @LoadBalanced
    // RestTemplate被Ribbon代理
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
