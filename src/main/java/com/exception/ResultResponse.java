package com.exception;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiehang
 * @date 2025/5/2 20:07
 *
 * 自定义全局响应类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultResponse<T> {

    private String code;
    private String mesage;
    private Object data;


    /**
     * 成功
     *
     * @param data
     * @return
     */
    public static ResultResponse success(Object data) {
        ResultResponse response = new ResultResponse();
        response.setCode(ExceptionEnum.SUCCESS.getCode());
        response.setMesage(ExceptionEnum.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    public static ResultResponse error(String code, String message) {
        return new ResultResponse(code, message, null);
    }

    public static ResultResponse error(ExceptionEnum exceptionEnum) {
        return new ResultResponse(ExceptionEnum.BODY_NOT_MATCH.getCode(),ExceptionEnum.BODY_NOT_MATCH.getMessage(), null);
    }

    public static ResultResponse error(String code, String message, Object data) {
        ResultResponse response = new ResultResponse();
        response.setCode(code);
        response.setMesage(message);
        response.setData(data);
        return response;
    }

    public static ResultResponse error(String message) {
        ResultResponse response = new ResultResponse();
        response.setCode(ExceptionEnum.INTERNAL_SERVER_ERROR.getCode());
        response.setMesage(message);
        response.setData(null);
        return response;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
