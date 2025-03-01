package com.myspringboot.serviceImpl;

import com.myspringboot.pojo.PurchaseRecordPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author xiehang
 * @create 2022-08-08 23:20
 */
@Service
public class TaskService implements com.myspringboot.service.TaskService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    //每天凌晨一点开始执行任务
    @Scheduled(cron = "0 0 1 * * ?")
    public void purchaseTask() {
        //
        Set<String> productIdList = stringRedisTemplate.opsForSet().members("product_schedule_set");
        List<PurchaseRecordPo> prpList = new ArrayList<>();


    }
}
