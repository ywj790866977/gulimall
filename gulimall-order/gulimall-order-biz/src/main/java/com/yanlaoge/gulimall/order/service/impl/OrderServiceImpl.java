package com.yanlaoge.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;
import com.yanlaoge.common.utils.RedisUtil;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.common.vo.MemberRespVo;
import com.yanlaoge.gulimall.cart.feign.CartFeignService;
import com.yanlaoge.gulimall.cart.vo.CartItem;
import com.yanlaoge.gulimall.member.entity.MemberReceiveAddressEntity;
import com.yanlaoge.gulimall.member.feign.MemberFeignService;
import com.yanlaoge.gulimall.order.constant.OrderConstant;
import com.yanlaoge.gulimall.order.dao.OrderDao;
import com.yanlaoge.gulimall.order.entity.OrderEntity;
import com.yanlaoge.gulimall.order.interceptor.LoginInterceptor;
import com.yanlaoge.gulimall.order.service.OrderService;
import com.yanlaoge.gulimall.order.vo.MemberAddressVo;
import com.yanlaoge.gulimall.order.vo.OrderConfirmVo;
import com.yanlaoge.gulimall.order.vo.OrderItemVo;
import com.yanlaoge.gulimall.ware.feign.WareFeignService;
import com.yanlaoge.gulimall.ware.vo.SkuHasStockVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


@Service("orderService")
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Resource
    private MemberFeignService memberFeignService;
    @Resource
    private CartFeignService cartFeignService;
    @Resource
    private ThreadPoolExecutor executor;
    @Resource
    private WareFeignService wareFeignService;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @SneakyThrows
    @Override
    public OrderConfirmVo confirmOrder() {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginInterceptor.threadLocal.get();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 1. 查询地址
        CompletableFuture<Void> addressFurure = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            confirmVo.setAddress(getMemberAddressVos(memberRespVo));
        }, executor);

        //2.获取选中购物项
        CompletableFuture<Void> orderItemFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            confirmVo.setItems(getOrderItemVos());
        }, executor).thenRunAsync(()->{
            List<Long> collect = confirmVo.getItems().stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            ResponseVo<List<SkuHasStockVo>> skuHasStockResp = wareFeignService.getSkuHasStock(collect);
            if(skuHasStockResp == null || skuHasStockResp.getCode() != 0){
                log.error("[confirmOrder] getSkuHasStock is Exception res = {}",skuHasStockResp);
            }
            List<SkuHasStockVo> skuHasStockVos = skuHasStockResp.getData();
            if(!CollectionUtils.isEmpty(skuHasStockVos)){
                Map<Long, Boolean> map = skuHasStockVos.stream()
                        .collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
                confirmVo.setStocks(map);
            }
        },executor);
        //3. 用户积分信息
        confirmVo.setIntegration(memberRespVo.getIntegration());
        //4. 防重令牌
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisUtil.set(OrderConstant.USER_ORDER_TOKEN_PRE+memberRespVo.getId(),token,30);
        confirmVo.setOrderToken(token);
        CompletableFuture.allOf(addressFurure,orderItemFuture).get();
        return confirmVo;
    }

    private List<OrderItemVo> getOrderItemVos() {
        ResponseVo<List<CartItem>> cartResp = cartFeignService.getUserCartItems();
        if(cartResp == null || cartResp.getCode() != 0){
            log.error("[confirmOrder] getUserCartItems is Exception res = {} ",cartResp);
//            ResponseHelper.execption(12001,"调用服务异常");
            return null;
        }
        return cartResp.getData().stream().map(item -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            BeanUtils.copyProperties(item, orderItemVo);
            return orderItemVo;
        }).collect(Collectors.toList());
    }

    private List<MemberAddressVo> getMemberAddressVos(MemberRespVo memberRespVo) {
        ResponseVo<List<MemberReceiveAddressEntity>> addressResp = memberFeignService.getAddress(memberRespVo.getId());
        if(addressResp == null || addressResp.getCode() != 0 ){
            log.error("[confirmOrder] getAddress is Exception res={}",addressResp);
//            ResponseHelper.execption(12001,"调用服务异常");
            return null;
        }
        return addressResp.getData().stream().map(item -> {
            MemberAddressVo memberAddressVo = new MemberAddressVo();
            BeanUtils.copyProperties(item, memberAddressVo);
            return memberAddressVo;
        }).collect(Collectors.toList());
    }

}