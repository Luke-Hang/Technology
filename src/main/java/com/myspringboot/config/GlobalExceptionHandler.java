package com.myspringboot.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理
 * 如何在Spring Boot中实现全局异常处理？
 * 1. 创建一个异常处理类，使用@ControllerAdvice注解
 * 2. 使用@ExceptionHandler注解，指定要处理的异常类型
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e){
        return ResponseEntity.status(500).body(e.getMessage());
    }
}
