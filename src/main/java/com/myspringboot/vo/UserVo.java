package com.myspringboot.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xiehang
 * @create 2022-08-03 9:48
 */
@Data
public class UserVo implements Serializable {
    private static final long serialVersionUID = -7569240440795229662L;


    private Long id;
    private String userName=null;
    private String note = null;

    public UserVo(long id, String userName, String note) {
        this.id = id;
        this.userName = userName;
        this.note = note;
    }
}
