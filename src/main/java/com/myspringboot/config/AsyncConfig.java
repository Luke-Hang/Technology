//package com.myspringboot.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.AsyncConfigurer;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.Executor;
//
///**
// * @author xiehang
// * @create 2022-08-01 22:50
// *
// * @EnableAsync
// * 如果Java配置文件标注它，那么Spring就会开启异步可用，这样就可以使用注解@Async驱动Spring使用异步调用
// *
// */
//@Configuration
////开启Spring线程异步
//@EnableAsync
//public class AsyncConfig implements AsyncConfigurer {
//
////    @Override
////    public Executor getAsyncExecutor(){
////        ThreadPoolTaskExecutor taskExecutor=new ThreadPoolTaskExecutor();
////        //设置核心线程数
////        taskExecutor.setCorePoolSize(10);
////        //设置最大线程数
////        taskExecutor.setMaxPoolSize(30);
////        //设置线程队列大小
////        taskExecutor.setQueueCapacity(2000);
////        //线程初始化
////        taskExecutor.initialize();
////        return taskExecutor;
////    }
//}
