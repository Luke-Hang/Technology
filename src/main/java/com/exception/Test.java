package com.exception;

import com.Equals.Person;
import com.myspringboot.model.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author xiehang
 * @date 2025/5/2 20:32
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(testGlobalExceptionHandler());
        System.out.println(testGlobalExceptionHandler2());
    }


    public static ResultResponse testGlobalExceptionHandler() {
        Person person = new Person();
        try {
            //person.setName("zhangsan");
            //如果姓名为空就手动抛出一个自定义的异常！
            person.getName().equals("zhangsan");
        } catch (Exception e) {
            return ResultResponse.error(ExceptionEnum.USER_NOT_EXIST, person);
        }
        return ResultResponse.success(person);
    }

    public static ResultResponse testGlobalExceptionHandler2() {
        String str = null;
        try {
            str.equals("111");
        } catch (Exception e) {
            return ResultResponse.error(ExceptionEnum.INTERNAL_SERVER_ERROR, str);
        }
        return ResultResponse.success(str);
    }
}
