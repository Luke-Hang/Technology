package com.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author xiehang
 * @date 2024/12/30 21:39
 */
@Component
public class FiledInject {

    @Autowired
    private String message;

    @Resource(name = "userinfo", type = UserInfo.class)
    private UserInfo user;

    @Autowired
    @Qualifier("myUser")
    private MyUser myUser;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
