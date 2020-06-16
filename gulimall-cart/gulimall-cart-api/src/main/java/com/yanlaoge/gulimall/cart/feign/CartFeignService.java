package com.yanlaoge.gulimall.cart.feign;

import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.cart.vo.CartItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author 好人
 * @date 2020-06-14 14:59
 **/
@FeignClient("gulimall-cart")
public interface CartFeignService {
    @GetMapping("/getUserCartItems")
    @ResponseBody
    ResponseVo<List<CartItem>> getUserCartItems();
}
