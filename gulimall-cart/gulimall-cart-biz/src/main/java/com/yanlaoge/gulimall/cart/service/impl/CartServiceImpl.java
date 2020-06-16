package com.yanlaoge.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yanlaoge.common.utils.*;
import com.yanlaoge.gulimall.cart.constant.CartConstant;
import com.yanlaoge.gulimall.cart.controller.CartController;
import com.yanlaoge.gulimall.cart.enums.CartStatusEnum;
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
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

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
        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String tempCartKey = CartConstant.CART_PREFIX + userInfoTo.getUserkey();
        if (userInfoTo.getUserId() == null) {
            // 没登录
            cart.setItems(getCartItems(tempCartKey));
            return cart;
        }
        // 登录的购物车
        // 判断临时购物车有无数据,进行合并
        List<CartItem> tempCarts = getCartItems(tempCartKey);
        if(!CollectionUtils.isEmpty(tempCarts)){
            tempCarts.forEach(item->{
                addToCart(item.getSkuId(),item.getCount());
            });
            redisUtil.del(tempCartKey);
        }
        cart.setItems(getCartItems(CartConstant.CART_PREFIX + userInfoTo.getUserId()));
        return cart;
    }

    @Override
    public void checkCartItem(Long skuId, Integer check) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);
        redisUtil.hSet(getCartKey(),skuId.toString(),JSON.toJSONString(cartItem));
    }

    @Override
    public void countItem(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        redisUtil.hSet(getCartKey(),skuId.toString(),JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        redisUtil.hDel(getCartKey(),skuId.toString());
    }

    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId() == null){
            return null;
        }
        String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        List<CartItem> cartItems = getCartItems(cartKey);
//        ServiceAssert.isEmpty(cartItems,13001,"购物车为空");
        if(CollectionUtils.isEmpty(cartItems)){
            return null;
        }
        return cartItems.stream().filter(CartItem::getCheck).map(this::apply).collect(Collectors.toList());
    }

    private List<CartItem> getCartItems(String cartKey) {
        List<Object> objects = redisUtil.hAllGet(cartKey);
        if(!CollectionUtils.isEmpty(objects)){
             return objects.stream().map(item ->
                 JSON.parseObject((String) item, CartItem.class)).collect(Collectors.toList());

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

    private CartItem apply(CartItem item) {
        ResponseVo<BigDecimal> responseVo = productFeignService.getSkuPrice(item.getSkuId());
        if (responseVo == null || responseVo.getCode() != 0) {
            log.error("[getUserCartItems] getSkuPrice is Exception  res = {}", responseVo);
            ResponseHelper.execption(
                    CartStatusEnum.NOT_REMOTE_GETPRICE.getCode(), CartStatusEnum.NOT_REMOTE_GETPRICE.getMsg());
        }
        item.setPrice(responseVo.getData());
        return item;
    }
}
