package com.yanlaoge.gulimall.cart.controller;

import com.yanlaoge.common.utils.ResponseHelper;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.cart.interceptor.CartInterceptor;
import com.yanlaoge.gulimall.cart.service.CartService;
import com.yanlaoge.gulimall.cart.to.UserInfoTo;
import com.yanlaoge.gulimall.cart.vo.Cart;
import com.yanlaoge.gulimall.cart.vo.CartItem;
import org.bouncycastle.math.raw.Mod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author rubyle
 */
@Controller
public class CartController {

    @Resource
    private CartService cartService;

    @GetMapping("/cart.html")
    public String cartList(Model model) {
        Cart cart =  cartService.getCart();
        model.addAttribute("cart",cart);
        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes model) {
        cartService.addToCart(skuId, num);
        model.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    @GetMapping("/addToCartSuccess.html")
    public String successPage(@RequestParam("skuId") Long skuId,Model model){
        CartItem cartItem =  cartService.getCartItem(skuId);
        model.addAttribute("item",cartItem);
        return "success";
    }

    @GetMapping("checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId,@RequestParam("check") Integer check){
        cartService.checkCartItem(skuId,check);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("countItem")
    public String countItem(@RequestParam("skuId") Long skuId,@RequestParam("num") Integer num){
        cartService.countItem(skuId,num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/getUserCartItems")
    @ResponseBody
    public ResponseVo<List<CartItem>> getUserCartItems(){
        return ResponseHelper.success(cartService.getUserCartItems());
    }
}
