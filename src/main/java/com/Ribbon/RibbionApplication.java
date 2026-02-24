package com.Ribbon;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author xiehang
 * @date 2025/4/5 8:18
 */
@SpringBootApplication
public class RibbionApplication {

    //使用 RestTemplate 实现负载均衡，
    // Ribbon 常与 RestTemplate 结合使用，通过 @LoadBalanced 注解为 RestTemplate 添加负载均衡功能。
    @Bean
    @LoadBalanced// 开启负载均衡功能
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
