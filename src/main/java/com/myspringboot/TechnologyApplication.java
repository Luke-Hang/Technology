package com.myspringboot;

import com.myspringboot.springcloud.model.Dept;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//开启自动配置
//@EnableAutoConfiguration
//开启使用注解驱动定时任务的机制
//@EnableScheduling
//开启EurekaServer服务
//@EnableEurekaServer
//@SpringCloudApplication
//@SpringBootApplication(scanBasePackages = "com.myspringboot")
@SpringBootApplication
public class TechnologyApplication {
    public static void main(String[] args) {
        SpringApplication.run(TechnologyApplication.class, args);
    }

}
