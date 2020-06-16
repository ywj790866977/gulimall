package com.yanlaoge.gulimall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 好人
 * @date 2020-06-14 10:53
 **/
@Controller
public class PageController {

    @GetMapping("/{page}.html")
    public String order(@PathVariable("page") String page){
        return page;
    }
}
