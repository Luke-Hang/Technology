package com.myspringboot.service;

import com.myspringboot.vo.UserVo;

/**
 * @author xiehang
 * @create 2022-08-03 9:22
 */
public interface ActiveMqUserService {

    public void sendUser(UserVo userVo);

    public void receiveUser(UserVo userVo);
}
