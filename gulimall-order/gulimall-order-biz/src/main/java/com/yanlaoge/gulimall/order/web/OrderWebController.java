package com.yanlaoge.gulimall.order.web;

import com.yanlaoge.gulimall.order.service.OrderService;
import com.yanlaoge.gulimall.order.vo.OrderConfirmVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

/**
 * @author 好人
 * @date 2020-06-14 12:03
 **/
@Controller
public class OrderWebController {
    @Resource
    private OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", orderConfirmVo);
        return "confirm";
    }
}
