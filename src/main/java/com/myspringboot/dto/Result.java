package com.myspringboot.dto;

import lombok.Data;

/**
 * @author xiehang
 * @create 2022-08-03 9:32
 */
@Data
public class Result{

    private boolean success = false;
    private String message = null;

    public Result() {
    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
