package com.exception;

/**
 * @author xiehang
 * @date 2025/5/2 19:44
 *
 * 自定义异常接口类
 * 为了代码解耦，创建一个接口类出来，定义自定义接口所需要的方法
 *
 */
public interface BaseErrorInfoInterface {
    String getCode();

    String getMessage();

}
