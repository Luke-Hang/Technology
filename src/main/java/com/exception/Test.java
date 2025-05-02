package com.exception;

import com.Equals.Person;

/**
 * @author xiehang
 * @date 2025/5/2 20:32
 *
 *
 * 1.自定义异常接口类 BaseErrorInfoInterface
 * 2.自定义异常枚举类 ExceptionEnum implements BaseErrorInfoInterface
 * 3.自定义异常类 BaseException extends RuntimeException
 * 4.自定义全局异常处理类 GlobalExceptionHandler
 * 5.自定义全局响应类 ResultResponse
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(testGlobalExceptionHandler());
        System.out.println(testGlobalExceptionHandler2());
    }


    public static ResultResponse testGlobalExceptionHandler() {
        Person person = new Person();
        //person.setName("zhangsan");
        //如果姓名为空就手动抛出一个自定义的异常！
        if (person.getName() == null) {
            throw new BaseException(ExceptionEnum.USER_NOT_EXIST);
            /*
            抛出自定义异常BaseException，被自定义全局异常处理类GlobalExceptionHandler baseExceptionHandler()捕获，
            return ResultResponse.error(e.getCode(), e.getMessage());
            即  public static ResultResponse error(String code, String message) {
                    return new ResultResponse(code, message, null);
               }
            返回结果为：
                {
                    "code":"1003",
                    "mesage":"该用户不存在",
                    "data":null
                }

             */
        } else {
            return ResultResponse.success("调用成功");
        }
    }

    public static ResultResponse testGlobalExceptionHandler2() {
        String str = null;
        try {
            str.equals("111");
            /**
             * str为null，会抛出空指针异常，被自定义全局异常处理类GlobalExceptionHandler nullPointerExceptionHandler()捕获
             * return ResultResponse.error(ExceptionEnum.BODY_NOT_MATCH);
             *
             * public static ResultResponse error(ExceptionEnum exceptionEnum) {
             *  return new ResultResponse(ExceptionEnum.BODY_NOT_MATCH.getCode(),ExceptionEnum.BODY_NOT_MATCH.getMessage(), null);
             * }
             * 返回结果为：
                 {
                     "code":"1003",
                     "mesage":"该用户不存在",
                     "data":null
                 }
             *
             */
        } catch (Exception e) {
            return ResultResponse.error(ExceptionEnum.BODY_NOT_MATCH);
        }
        return ResultResponse.success(str);
    }
}
