//package com.myspringboot.config;
//
//import org.springframework.amqp.core.Queue;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author xiehang
// * @create 2022-08-05 0:31
// */
//
///**
// *
// * @Component与@Configuration区别：
// *
// * @Configuration本质上还是@Component。
// * @Configuration标记的类必须符合下面的要求：
// * 1.配置类不能是 final 类、都必须声明为static
// * 2.配置注解通常为了通过 @Bean 注解生成 Spring 容器管理的类，
// * 3.配置类必须是非本地的（即不能在方法中声明，不能是 private）
// * @Configuration:会当做配置类，但会为其生成CGLIB代理class
// * @Configuration 中所有带 @Bean 注解的方法都会被动态代理，因此调用该方法返回的都是同一个实例。
// *
// * 在获取当前类名时，使用@Component获取的是当前类名；而@Configuration获取的是当前类名+唯一标识(CGLIB代理)
// * @Configuration 中所有带 @Bean 注解的方法都会被动态代理，因此调用该方法返回的都是同一个实例。
// */
//@Configuration
//public class RabbitmqConfig {
//    // 消息队列名称
//    @Value("${rabbitmq.queue.msg}")
//    private String msgQueueName = null;
//
//    //用户队列名称
//    @Value("${rabbitmq.queue.user}")
//    private String userQueueName=null;
//
//    @Bean
//    public Queue createQueueMsg(){
//        // 创建字符串消息队列，boolean值代表是否持久化消息
//        return new Queue(msgQueueName, true);
//    }
//
//    @Bean
//    public Queue createQueueUser(){
//        // 创建用户消息队列，boolean值代表是否持久化消息
//        return new Queue(userQueueName, true);
//    }
//}
