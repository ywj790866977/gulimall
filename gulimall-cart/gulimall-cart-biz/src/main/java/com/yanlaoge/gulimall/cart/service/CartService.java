package com.yanlaoge.gulimall.cart.service;

import com.yanlaoge.gulimall.cart.vo.Cart;
import com.yanlaoge.gulimall.cart.vo.CartItem;

/**
 * @author rubyle
 */
public interface CartService {
    /**
     * 添加购物车
     *
     * @param skuId skuid
     * @param num   num
     * @return item
     */
    CartItem addToCart(Long skuId, Integer num);

    /**
     * 根绝skuId获取
     *
     * @param skuId skuid
     * @return item
     */
    CartItem getCartItem(Long skuId);

    /**
     * 获取购物车
     *
     * @return cart
     */
    Cart getCart();

}
