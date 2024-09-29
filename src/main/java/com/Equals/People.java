package com.Equals;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xiehang
 * @date 2024/8/26 15:46
 */
@Getter
@Setter
public class People {

    private int id;
    private String name;
    private int age;

    public People(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public People() {
    }
}
