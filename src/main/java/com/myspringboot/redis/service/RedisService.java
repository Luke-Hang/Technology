package com.myspringboot.redis.service;

import com.myspringboot.model.UserModel;

import java.util.List;

/**
 * @author xiehang
 * @create 2022-11-22 22:56
 */
public interface RedisService {

    void addString();

    void addList();

    List<UserModel> getListData();

    void deleteOverExes();
}
