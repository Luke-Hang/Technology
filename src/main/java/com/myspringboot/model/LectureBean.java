package com.myspringboot.model;

import lombok.Data;
import org.hamcrest.Condition;

/**
 * @author xiehang
 * @create 2022-08-17 23:34
 *
 * 课程表实体
 */
public class LectureBean {
    private Integer id;
    private String lectureName;
    private String note;

    public LectureBean() {

    }

   public LectureBean(Integer id, String lectureName, String note) {
        this.id = id;
        this.lectureName = lectureName;
        this.note = note;
    }

    public LectureBean(String str) {
        System.out.println(str);
    }

    public Integer getId() {
        return id;
    }

    public String getLectureName() {
        return lectureName;
    }

    public String getNote() {
        return note;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
