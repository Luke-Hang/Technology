package com.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author xiehang
 * @date 2025/5/2 20:03
 * <p>
 * 自定义全局异常处理类
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    //静态变量，系统log
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * 处理自定义的业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BaseException.class)
    @ResponseBody
    public ResultResponse baseExceptionHandler(BaseException e) {
        logger.error("发生业务异常！原因是：{}", e.getMessage());
        return ResultResponse.error(e.getCode(), e.getMessage());
    }


    @ExceptionHandler(value = SQLException.class)
    @ResponseBody
    public ResultResponse sqlExceptionHandler(SQLException e) {
        logger.error("发生业务异常！原因是：{}", e.getMessage());
        if (e instanceof SQLIntegrityConstraintViolationException) {
            return ResultResponse.error("该数据有关联数据，操作失败!");
        }
        return ResultResponse.error("数据库异常，操作失败!");
    }

    /**
     * 处理空指针的异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public ResultResponse nullPointerExceptionHandler(NullPointerException e) {
        logger.error("发生空指针异常！原因是:", e);
        return ResultResponse.error(ExceptionEnum.BODY_NOT_MATCH);
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
        return ResultResponse.error(ExceptionEnum.INTERNAL_SERVER_ERROR);
    }
}
