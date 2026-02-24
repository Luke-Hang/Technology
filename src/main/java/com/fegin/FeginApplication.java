package com.fegin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author xiehang
 * @date 2025/4/5 8:03
 */
@SpringBootApplication
@EnableFeignClients// 启用 Feign 客户端
public class FeginApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeginApplication.class);
    }
}
