package com.yanlaoge.gulimall.client.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rubyle
 */
@Controller
public class HelloController {
    @Value("${sso.server.url}")
    private String ssoSerber;

    @GetMapping("/hello")
    @ResponseBody
    public String hello(){

        return "hello";
    }

    @GetMapping("/list")
    public String emp(Model model, HttpSession session){
        Object loginUser = session.getAttribute("loginUser");
        if(loginUser ==null){
            return "redirect:"+ssoSerber+"?redirect_url=http://sso.com:8081/list";
        }
        List<String> list = new ArrayList<>();
        list.add("tom");
        list.add("超哥");
        model.addAttribute("emps",list);
        return "list";
    }
}
