package com.myspringboot.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xiehang
 * @create 2022-08-17 23:35
 *
 * 课程成绩表实体
 */
@Data
public class StudentLectureBean {
    private int id;

    /**学生编号**/
    private Integer StudentId;

    /**课程名称**/
    private LectureBean lecture;

    /**课程分数**/
    private BigDecimal grade;

    private String note;
}
