package com.myspringboot.redis.raffle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiehang
 * @date 2024/8/6 21:14
 */
@Component
public class JdTask {

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String PRIZE_KEY = "jd:goods";

    /**
     * 提前先把数据刷新到redis缓存中。
     *
     * PostConstruct Java jdk提供的注解,实现Bean初始化之前和销毁之前的自定义操作
     * 该注解的方法在整个Bean初始化中的执行顺序：
     *  Constructor(构造方法) -> @Autowired(依赖注入) -> @PostConstruct(注释的初始化方法)
     * 该注解的功能：当依赖注入完成后用于执行初始化的方法，并且只会被执行一次
     */
    @PostConstruct
    public void init(){
        /**
         * userService.doSomething(),userService注入后执行一些初始化操作
         */
        //redisTemplate注入后执行一些初始化操，这里执行redisTemplate注入后执行，提前先把数据刷新到redis缓存中的操作
        Boolean bo = redisTemplate.hasKey(PRIZE_KEY);
        if (!bo) {
            List<String> crowds = this.prize();
            crowds.forEach(t->redisTemplate.opsForSet().add(PRIZE_KEY,t));
        }
    }

    private List<String> prize() {
        List<String> list=new ArrayList<>();
        //10个京豆，概率10%
        for (int i = 0; i < 10; i++) {
            list.add("10-"+i);
        }
        //5个京豆，概率20%
        for (int i = 0; i < 20; i++) {
            list.add("5-"+i);
        }
        //1个京豆，概率60%
        for (int i = 0; i < 60; i++) {
            list.add("1-"+i);
        }
        //0个京豆，概率10%
        for (int i = 0; i < 10; i++) {
            list.add("0-"+i);
        }
        return list;
    }
}
