package com.myspringboot.controller;

import com.myspringboot.dto.Result;
import com.myspringboot.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author xiehang
 * @create 2022-08-07 18:15
 */
@RestController
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    // 定义JSP视图
    @GetMapping("/test")
    public ModelAndView testPage() {
        ModelAndView mv = new ModelAndView("test");
        return mv;
    }



    @PostMapping("/purchase")
    public Result purchase(Long userId, Long productId, Integer quantity) {
        boolean success = purchaseService.purchaseRedis(userId, productId, quantity);
        String message = success ? "抢购成功" : "抢购失败";
        Result result = new Result(success, message);
        return result;
    }
}
