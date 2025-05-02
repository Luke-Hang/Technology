package com.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xiehang
 * @date 2025/5/2 20:03
 * <p>
 * 自定义全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    //静态变量，系统log
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * 处理自定义的业务异常
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = BaseException.class)
    @ResponseBody
    public ResultResponse baseExceptionHandler(HttpServletRequest request, BaseException e) {
        logger.error("发生业务异常！原因是：{}", e.getMessage());
        return ResultResponse.error(e.getCode(), e.getMessage());
    }


    /**
     * 处理空指针的异常
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public ResultResponse exceptionHandler(HttpServletRequest request, NullPointerException e) {
        logger.error("发生空指针异常！原因是:", e);
        return ResultResponse.error(ExceptionEnum.INTERNAL_SERVER_ERROR, e);
    }

    /**
     * 处理其他异常
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultResponse exceptionHandler(HttpServletRequest request, Exception e) {
        logger.error("未知异常！原因是:", e);
        return ResultResponse.error(ExceptionEnum.INTERNAL_SERVER_ERROR, e);
    }

}
