package com.yanlaoge.gulimall.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @GetMapping("/login.html")
    public String login(@RequestParam("redirect_url") String url, Model model){
        model.addAttribute("url",url);
        return "login";
    }

    @GetMapping("/doLogin")
    public String doLogin(String username, String password, String url, HttpSession session){
        if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password) ){
            session.setAttribute("loginUser",username);
            return "redirect:"+url;
        }
        return "login";
    }
}
