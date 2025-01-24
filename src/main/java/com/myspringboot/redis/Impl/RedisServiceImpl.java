package com.myspringboot.redis.Impl;

import com.myspringboot.dao.ProductDao;
import com.myspringboot.model.UserModel;
import com.myspringboot.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xiehang
 * @create 2022-11-22 22:56
 */
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductDao productDao;




    @Override
    public void addString() {
        //设置值
        stringRedisTemplate.opsForValue().set("aa", "小明");
        //获取值
        String aa = stringRedisTemplate.opsForValue().get("aa");
        //设置值且设置超时时间
        stringRedisTemplate.opsForValue().set("Middle", "Yu", 3, TimeUnit.DAYS);
        String middle = stringRedisTemplate.opsForValue().get("Middle");
        System.out.println(middle);
        //删除数据
        Boolean delete = stringRedisTemplate.delete("Middle");
        System.out.println(delete);


        redisTemplate.opsForValue().set("name","tom");
        redisTemplate.opsForValue().get("name");


        redisTemplate.opsForValue().set("name","tom",10, TimeUnit.SECONDS);
        //由于设置的是10秒失效，十秒之内查询有结果，十秒之后返回为null
        redisTemplate.opsForValue().get("name");
    }


    /**
     * 调用算法将计算结果给数据库和redis各存一份
     */
    @Override
    public void addList() {
        List<UserModel> dataList = getPersonList();
        //将计算结果存入数据库中
        productDao.insertDataList(dataList);
        //redis存储list
        redisTemplate.opsForList().rightPush("dataList", dataList);
        //给redis设置时间，第一个参数是key，第二个参数是值，第三个参数是时间颗粒度转换
        redisTemplate.expire("dataList", 3, TimeUnit.DAYS);
    }

    /**
     * 设置redis失效时间是3天，超过三天后从数据库里面获取结果
     * @return
     */
    @Override
    public List<UserModel> getListData() {
        if (redisTemplate.hasKey("dataList")) {
            //如果有就查询redis里这个list集合（第一个参数是key,0,-1是查询所有）
            List dataList = redisTemplate.opsForList().range("dataList", 0, -1);
            //返回这个集合
            return dataList;
        } else {
            //从mysql里查询这个集合
            List<UserModel> dataList = productDao.findDataList();
            //存入redis
            redisTemplate.opsForList().leftPushAll("dataList", dataList);
            //给redis设置时间，第一个参数是key，第二个参数是值，第三个参数是时间,这里设置失效时间是3天
            redisTemplate.expire("dataList", 3, TimeUnit.DAYS);
            //返回这个集合
            return dataList;
        }
    }

    @Override
    public void deleteOverExes() {
        //循环删除，每次删除5000条数据
    }

    private List<UserModel> getPersonList() {
        return null;
    }
}
