package com.yanlaoge.gulimall.member.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author 好人
 * @date 2020-07-04 17:50
 **/
@Controller
public class MemberWebController {

    @GetMapping("/memberOder.html")
    public String memberOrderPage(){

        return "orderList";
    }
}
