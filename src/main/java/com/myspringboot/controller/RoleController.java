package com.myspringboot.controller;

import com.myspringboot.model.RoleBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author xiehang
 * @create 2022-08-24 9:17
 */
//标识控制器
@Controller
public class RoleController {

    @RequestMapping("/role/getRole")//dispatcherServlet 路径匹配时,进入该方法
    @ResponseBody //标注把结果转换为Json
    public RoleBean getRole(@RequestParam("id") int id){
        return null;
    }
}

