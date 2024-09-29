package com.JsonFormate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiehang
 * @date 2024/7/25 9:03
 */
@Data
//全参构造方法
@AllArgsConstructor
//无参构造方法
@NoArgsConstructor
public class Student {
    private int id;
    private String name;
    private int age;
    private double score;
}
