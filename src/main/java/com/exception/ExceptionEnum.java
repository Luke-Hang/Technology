package com.exception;

/**
 * @author xiehang
 * @date 2025/5/2 19:47
 *
 * 定义一个枚举类，实现上述接口，重写上述接口的两个方法来操作这个枚举类内部的各个具体枚举值
 */
public enum ExceptionEnum implements BaseErrorInfoInterface {

    SUCCESS("200", "成功"),
    BODY_NOT_MATCH("400", "数据格式不匹配"),
    NOT_FOUND("404", "找不到资源"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误"),
    USER_NOT_EXIST("1001", "用户不存在");


    private String code;

    private String message;


    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
