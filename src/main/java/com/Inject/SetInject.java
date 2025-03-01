package com.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xiehang
 * @date 2024/12/30 21:36
 */
@Component
public class SetInject {

    private String message;

    @Autowired
    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


}
