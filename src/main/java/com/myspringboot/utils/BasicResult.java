package com.myspringboot.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiehang
 * @create 2022-08-03 12:29
 */
@Data
@NoArgsConstructor
public class BasicResult<T> {
    /**
     *  响应状态
     */
    private Integer status;
    /**
     *  响应编码
     */
    private String message;
    /**
     *  返回数据
     */
    private T data;

    public BasicResult(Integer code, String message, T data) {
    }


    /**
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BasicResult<T> success(T data){
        return new BasicResult<T>(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMessage(),data);
    }

    /**
     *
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BasicResult<T> success(String message, T data){
        return new BasicResult<T>(ResultEnum.SUCCESS.getCode(),message,data);
    }

    public static <T> BasicResult<T> instance(Integer status, String message, T data){
        BasicResult<T> result=new BasicResult<>();
        result.setStatus(status);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

}
