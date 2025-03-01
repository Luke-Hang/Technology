package com.JsonFormate;

import lombok.Data;

/**
 * @author xiehang
 * @date 2025/1/14 19:41
 */
@Data
public class MyBean {
    private String name;
    private int age;

    @Override
    public String toString() {
        return "MyBean{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
