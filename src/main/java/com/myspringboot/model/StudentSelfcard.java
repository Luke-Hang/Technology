package com.myspringboot.model;

import lombok.Data;

import java.util.Date;

/**
 * @author xiehang
 * @create 2022-08-17 23:30
 */
@Data
public class StudentSelfcard {

    private int id;

    private int StudentId;
    private String native_;
    private Date issueDate;
    private Date endDate;
    private String note;

}
