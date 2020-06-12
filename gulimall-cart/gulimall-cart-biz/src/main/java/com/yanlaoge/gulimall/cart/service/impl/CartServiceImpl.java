package com.yanlaoge.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yanlaoge.common.utils.R;
import com.yanlaoge.common.utils.RedisUtil;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.cart.constant.CartConstant;
import com.yanlaoge.gulimall.cart.controller.CartController;
import com.yanlaoge.gulimall.cart.interceptor.CartInterceptor;
import com.yanlaoge.gulimall.cart.service.CartService;
import com.yanlaoge.gulimall.cart.to.UserInfoTo;
import com.yanlaoge.gulimall.cart.vo.Cart;
import com.yanlaoge.gulimall.cart.vo.CartItem;
import com.yanlaoge.gulimall.product.entity.SkuInfoEntity;
import com.yanlaoge.gulimall.product.feign.ProductFeignService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
        String cartKey = getCartKey();
        String json = (String) redisUtil.hGet(cartKey, skuId.toString());
        if (!StringUtils.isEmpty(json)) {
            CartItem cartItem = JSON.parseObject(json, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            redisUtil.hSet(cartKey, skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }

        return addNewCart(skuId, num, cartKey);
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        String cartKey = getCartKey();
        String o = (String) redisUtil.hGet(cartKey, skuId.toString());
        return JSON.parseObject(o, CartItem.class);
    }

    @Override
    public Cart getCart() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null) {
            // 没登录
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserkey();
            List<Object> objects = redisUtil.hAllGet(cartKey);
//            objects.stream().map(item->{
//                CartItem cartItem = new CartItem();
//            })
        }
        return null;
    }

    @SneakyThrows
    private CartItem addNewCart(Long skuId, Integer num, String cartKey) {
        CartItem cartItem = new CartItem();

        CompletableFuture<Void> getSkuInfo = getSkuInfo(skuId, num, cartItem);

        CompletableFuture<Void> getSkuAttr = getSkuAttr(skuId, cartItem);

        CompletableFuture.allOf(getSkuInfo, getSkuAttr).get();

        setCartRedis(skuId, cartItem, cartKey);
        return cartItem;
    }

    private void setCartRedis(Long skuId, CartItem cartItem, String cartKey) {
        String jsonString = JSON.toJSONString(cartItem);
        redisUtil.hSet(cartKey, skuId.toString(), jsonString);
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
                log.error("[getSkuAttr] productFeignService and getSkuSaleAttrValues method is error , res: {}", responseVo);
            }
            List<String> data = responseVo.getData();
            cartItem.setSkuAttr(data);
        }, executor);
    }

    private CompletableFuture<Void> getSkuInfo(Long skuId, Integer num, CartItem cartItem) {
        return CompletableFuture.runAsync(() -> {
            R r = productFeignService.info(skuId);
            if (r.getMapCode() != 0) {
                log.error("[getSkuInfo] remote is err ,res : {}", r);
            }
            SkuInfoEntity skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoEntity>() {
            });
            cartItem.setCheck(true);
            cartItem.setCount(num);
            cartItem.setImage(skuInfo.getSkuDefaultImg());
            cartItem.setPrice(skuInfo.getPrice());
            cartItem.setTitle(skuInfo.getSkuTitle());
            cartItem.setSkuId(skuId);
        }, executor);
    }
}
