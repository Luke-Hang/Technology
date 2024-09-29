package com.myspringboot.controllerpara;

import com.myspringboot.model.UserModel;
import com.myspringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiehang
 * @date 2023/3/10 18:06
 */
@RestController
@RequestMapping("/GetControlPara")
public class GetControlPara {
    @Autowired
    private UserService userService;

    /**
     * 无注解获取参数
     * 参数名需与HTTP请求参数名一致
     */
    @GetMapping("/no/annotation")
    @ResponseBody
    public UserModel getUserById(String userId, String userName, String userNote) {
        //http://localhost:8080/GetControlPara/no/annotation?userId=1&userName="zhangsan"
        UserModel userModel = new UserModel();
        userModel.setUserId(userId);
        userModel.setUserName(userName);
        userModel.setUserNote(userNote);
        return userModel;
    }

    /**
     * 使用@RequestParam来确定前后端参数名称的映射关系
     * user_id---userId
     * user_name---userName
     * user_note---userNote
     */
    @GetMapping("/annotation")
    @ResponseBody
    public UserModel getUserByName(@RequestParam("user_id") String userId,
                                   @RequestParam("user_name") String userName,
                                   @RequestParam("user_note") String userNote) {
        //http://localhost:8080/annotation?user_id=1&user_name=zhangsan&userNote=aa
        UserModel userModel = new UserModel();
        userModel.setUserId(userId);
        userModel.setUserName(userName);
        userModel.setUserNote(userNote);
        return userModel;
    }

    /**
     * 通过处理器映射和注解@PathVariable的组合获取URL参数，@PathVariable可以通过名称来获取参数
     */
    @GetMapping("/annotation/{userId}/{userNote}/{userName}")
    @ResponseBody
    public UserModel getUserNote(@PathVariable("userId")String userId,
                                 @PathVariable("userNote") String userNote,
                                 @PathVariable("userName") String userName) {
        //http://localhost:8080/annotation?
        // user_id=1&user_name=zhangsan
        UserModel userModel = new UserModel();
        userModel.setUserId(userId);
        userModel.setUserName(userName);
        userModel.setUserNote(userNote);
        return userModel;
    }

    /**
     * 传递JSON(@RequestBody),即传递对象userModel
     * @RequestBody接收前台传过来的userModel对象
     */
    @GetMapping("/insetUser")
    @ResponseBody
    public void insetUser(@RequestBody UserModel userModel) {
        userService.addUser(userModel);
    }

    @GetMapping("/requestArray")
    @ResponseBody
    public Map<String, Object> getUserNote(Integer[] intArr,
                                           Long[] longArr,
                                           String[] strArr) {
        Map<String, Object> map = new HashMap<>();
        map.put("intArr", intArr);
        map.put("longArr", longArr);
        map.put("strArr", strArr);
        return map;
    }

}
