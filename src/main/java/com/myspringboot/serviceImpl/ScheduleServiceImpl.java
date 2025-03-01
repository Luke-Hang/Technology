package com.myspringboot.serviceImpl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author xiehang
 * @create 2022-08-06 8:46
 */
@Service
public class ScheduleServiceImpl {

    //计数器
    int count1=1;
    int count2=2;
    int count3=3;

    //Scheduled预定的，单位是毫秒，这里设置每隔一秒执行一次
/*    @Scheduled(fixedRate  = 1000)
    //使用异步线程执行
    @Async
    public void job1(){
        System.out.println("【" +Thread.currentThread().getName()+"】"
                + "【job1】每秒钟执行一次，执行第【" + count1 + "】次");
        count1++;
    }*/

    //Scheduled预定的，单位是毫秒，这里设置每隔一秒执行一次
/*    @Scheduled(fixedRate  = 1000)
    //使用异步线程执行
    @Async
    public void job2(){
        System.out.println("【" +Thread.currentThread().getName()+"】"
                + "【job2】每秒钟执行一次，执行第【" + count2 + "】次");
        count2++;
    }*/


    //12:00-12:59每分钟执行一次
    @Scheduled(cron = "0 * 12 * * ?")
    @Async
    public void job3(){
        System.out.println("【" +Thread.currentThread().getName()+"】"
                + "【job3】每分钟执行一次，执行第【" + count3 + "】次");
        count3++;
    }
}
