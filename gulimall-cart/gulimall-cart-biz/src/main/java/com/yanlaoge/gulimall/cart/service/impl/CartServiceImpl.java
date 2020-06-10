package com.yanlaoge.gulimall.cart.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.yanlaoge.common.utils.R;
import com.yanlaoge.common.utils.RedisUtil;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.cart.constant.CartConstant;
import com.yanlaoge.gulimall.cart.controller.CartController;
import com.yanlaoge.gulimall.cart.interceptor.CartInterceptor;
import com.yanlaoge.gulimall.cart.service.CartService;
import com.yanlaoge.gulimall.cart.to.UserInfoTo;
import com.yanlaoge.gulimall.cart.vo.CartItem;
import com.yanlaoge.gulimall.product.entity.SkuInfoEntity;
import com.yanlaoge.gulimall.product.feign.ProductFeignService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author rubyle
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ProductFeignService productFeignService;
    @Resource
    private ThreadPoolExecutor executor;

    @SneakyThrows
    @Override
    public CartItem addToCart(Long skuId, Integer num) {
        CartItem cartItem = new CartItem();

        String cartKey = getCartKey();

        CompletableFuture<Void> getSkuInfo = getSkuInfo(skuId, num, cartItem);

        CompletableFuture<Void> getSkuAttr = getSkuAttr(skuId, cartItem);

        CompletableFuture.allOf(getSkuInfo,getSkuAttr).get();

        redisUtil.hSet(cartKey,skuId.toString(),cartItem);
        return cartItem;
    }

    private String getCartKey() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey;
        cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserkey();
        if (userInfoTo.getUserId() != null) {
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        }
        return cartKey;
    }

    private CompletableFuture<Void> getSkuAttr(Long skuId, CartItem cartItem) {
        return CompletableFuture.runAsync(() -> {
                ResponseVo<List<String>> responseVo = productFeignService.getSkuSaleAttrValues(skuId);
                if (responseVo.getCode() != 0) {
                    log.error("[] productFeignService and getSkuSaleAttrValues method is error , res: {}", responseVo);
                }
                List<String> data = responseVo.getData();
                cartItem.setSkuAttr(data);
            }, executor);
    }

    private CompletableFuture<Void> getSkuInfo(Long skuId, Integer num, CartItem cartItem) {
        return CompletableFuture.runAsync(() -> {
                R r = productFeignService.info(skuId);
                SkuInfoEntity skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoEntity>() {
                });
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setPrice(skuInfo.getPrice());
                cartItem.setSkuTitle(skuInfo.getSkuTitle());
                cartItem.setSkuId(skuId);
            }, executor);
    }
}
