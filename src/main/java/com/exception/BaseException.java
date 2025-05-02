package com.exception;

import lombok.Data;

/**
 * @author xiehang
 * @date 2025/5/2 19:52
 *
 * 自定义异常类
 *
 * 自定义一个异常类，就像空指针异常类、IO流异常类一样。此处自定义的异常类属于异常类，所以肯定是要继承一个异常类的，
 * 此处需要继承RuntimeException，原因如下：
 * RuntimeException对比Exception，他是在程序运行时才会爆出异常，在编译时是不会出现异常的，
 *
 * 这就表示，如果你throw了一个RuntimeException，不需要做额外操作；
 * 而throw一个Exception，程序会要求你try-catch，否则你根本启动不了程序，程序会提示（必须对其进行捕获或声明以便抛出）
 */
@Data
public class BaseException extends RuntimeException {


    //错误码
    private String code;
    //错误信息
    private String message;

    public BaseException() {
        super();
    }

    public BaseException(ExceptionEnum exceptionEnum) {
        this.code = exceptionEnum.getCode();
        this.message = exceptionEnum.getMessage();
    }
}
