package com.myspringboot.model;

import lombok.Data;

import java.io.File;

/**
 * @author xiehang
 * @date 2023/4/16 10:41
 */
@Data
public abstract class User {
    private static final long serialVersionUID = -1399276620826034194L;
    private String userId;
    private String userName;
    private String userNote;

    public abstract void getBean();
    public static void getBean25() {
        System.out.println(123);
    }
//    private abstract String sex;
//    public abstract void getBean2(){};
}

