package com.myspringboot.model;

import lombok.Data;

import java.util.List;

/**
 * @author xiehang
 * @create 2022-08-17 23:27
 *
 * 学生实体
 */
@Data
public class StudentBean {
    private int id;
    private String cnname;
    private int sex;
    private StudentSelfcard studentSelfcard;

    /**学生课程表**/
    private List<StudentLectureBean> studentLectureBeanList;
}
