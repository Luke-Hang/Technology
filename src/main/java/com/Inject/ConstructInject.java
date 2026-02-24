package com.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xiehang
 * @date 2024/12/30 21:38
 */
@Component
public class ConstructInject {

    private String message;

    @Autowired//构造器注入
    public ConstructInject(String message){
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
