package com.yanlaoge.gulimall.cart.controller;

import com.yanlaoge.gulimall.cart.interceptor.CartInterceptor;
import com.yanlaoge.gulimall.cart.service.CartService;
import com.yanlaoge.gulimall.cart.to.UserInfoTo;
import com.yanlaoge.gulimall.cart.vo.CartItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author rubyle
 */
@Controller
public class CartController {

    @Resource
    private CartService cartService;

    @GetMapping("/cart.html")
    public String cartList(HttpSession session) {

        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, Model model) {
        CartItem cartItem = cartService.addToCart(skuId, num);
        model.addAttribute("item",cartItem);
        return "success";
    }
}
