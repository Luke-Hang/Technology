package com.myspringboot.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author xiehang
 * @create 2022-08-01 8:51
 */
public class UserModel extends User implements Serializable{

    @Override
    public void getBean() {

    }
}
