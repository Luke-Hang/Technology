package com.myspringboot.utils;

/**
 * 局响应状态枚举
 * @author xiehang
 * @create 2022-08-03 12:22
 */
public enum ResultEnum implements IResult{

    /**
     * 错误
     */
    ERROR_500(500, "服务器未知错误"),
    ERROR_400(400, "错误请求"),

    SUCCESS(0,"接口调用成功"),
    VALIDATE_FAILED(-1,"参数校验失败"),
    COMMON_FAILED(2003,"接口调用失败"),
    FORBIDDEN(2004,"没有权限访问资源");


    private Integer code;
    private String message;


    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return ResultEnum.SUCCESS.code;
    }

    @Override
    public String getMessage() {
        return ResultEnum.SUCCESS.message;
    }
}
