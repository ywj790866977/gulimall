package com.yanlaoge.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.*;
import com.yanlaoge.common.vo.MemberRespVo;
import com.yanlaoge.gulimall.cart.feign.CartFeignService;
import com.yanlaoge.gulimall.cart.vo.CartItem;
import com.yanlaoge.gulimall.member.entity.MemberReceiveAddressEntity;
import com.yanlaoge.gulimall.member.feign.MemberFeignService;
import com.yanlaoge.gulimall.order.constant.OrderConstant;
import com.yanlaoge.gulimall.order.constant.OrderRespStatus;
import com.yanlaoge.gulimall.order.dao.OrderDao;
import com.yanlaoge.gulimall.order.entity.OrderEntity;
import com.yanlaoge.gulimall.order.entity.OrderItemEntity;
import com.yanlaoge.gulimall.order.eunms.OrderStatusEnum;
import com.yanlaoge.gulimall.order.interceptor.LoginInterceptor;
import com.yanlaoge.gulimall.order.service.OrderItemService;
import com.yanlaoge.gulimall.order.service.OrderService;
import com.yanlaoge.gulimall.order.vo.*;
import com.yanlaoge.gulimall.order.vo.MemberAddressVo;
import com.yanlaoge.gulimall.product.entity.SpuInfoEntity;
import com.yanlaoge.gulimall.product.feign.ProductFeignService;
import com.yanlaoge.gulimall.ware.feign.WareFeignService;
import com.yanlaoge.gulimall.ware.vo.*;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
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
    private ProductFeignService productFeignService;
    @Resource
    private OrderItemService itemService;
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
        }, executor).thenRunAsync(() -> {
            List<Long> collect = confirmVo.getItems().stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            ResponseVo<List<SkuHasStockVo>> skuHasStockResp = wareFeignService.getSkuHasStock(collect);
            if (skuHasStockResp == null || skuHasStockResp.getCode() != 0) {
                log.error("[confirmOrder] getSkuHasStock is Exception res = {}", skuHasStockResp);
            }
            List<SkuHasStockVo> skuHasStockVos = skuHasStockResp.getData();
            if (!CollectionUtils.isEmpty(skuHasStockVos)) {
                Map<Long, Boolean> map = skuHasStockVos.stream()
                        .collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
                confirmVo.setStocks(map);
            }
        }, executor);
        //3. 用户积分信息
        confirmVo.setIntegration(memberRespVo.getIntegration());
        //4. 防重令牌
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisUtil.set(OrderConstant.USER_ORDER_TOKEN_PRE + memberRespVo.getId(), token, 30);
        confirmVo.setOrderToken(token);
        CompletableFuture.allOf(addressFurure, orderItemFuture).get();
        return confirmVo;
    }

    /**
     * 提交订单
     * 不适用于使用 AT模式的分布式事务控制, 因为会将并发串行化
     *
     * @param vo vo
     * @return 订单
     */
    //@GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SubmitOrderResponseVo orderSubmit(OrderSubmitVo vo) {
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        //获取用户信息
        MemberRespVo memberRespVo = LoginInterceptor.threadLocal.get();
        //1. 原子验证令牌
        Long isOk = atomTokenCheck(vo, memberRespVo.getId());
        ServiceAssert.isFalse(isOk==1,OrderRespStatus.TOKEND_ERROR);
        //2. 创建订单
        CreateOrderTo orderTo = createOrder(vo);
        //3. 验价
        boolean isPrice =
                orderTo.getOrder().getPayAmount().subtract(vo.getPayPrice()).abs().doubleValue() < OrderConstant.PRICE_DIFFERENCE;
        ServiceAssert.isFalse(isPrice,OrderRespStatus.PRICE_ERROR);
        //4.保存订单
        saveOrder(orderTo);
        //6.锁定库存
        // 6.1 如果失败,发消息,让服务进行回滚
        // 6.2 库存自动解锁
        WareSkuLockVo wareSkuLockVo = builderWareLockVo(orderTo);
        ResponseVo<List<LockStockResVo>> lockRes = wareFeignService.orderLockStock(wareSkuLockVo);
        if (lockRes == null || lockRes.getCode() != 0) {
            log.error("[wareFeignService] remote method orderLockStock is error res={}", lockRes);
            ResponseHelper.execption(OrderRespStatus.STOCK_ERROR);
        }
        //7. 成功
        responseVo.setOrder(orderTo.getOrder());
        return responseVo;
    }

    /**
     * 构建锁定库存vo
     *
     * @param orderTo to
     * @return vo
     */
    private WareSkuLockVo builderWareLockVo(CreateOrderTo orderTo) {
        WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
        wareSkuLockVo.setOrderSn(orderTo.getOrder().getOrderSn());
        List<OrderItemLockVo> collect = orderTo.getOrderItems().stream().map(item -> {
            OrderItemLockVo orderItemLockVo = new OrderItemLockVo();
            orderItemLockVo.setCount(item.getSkuQuantity());
            orderItemLockVo.setSkuId(item.getSkuId());
            orderItemLockVo.setTitle(item.getSkuName());
            return orderItemLockVo;
        }).collect(Collectors.toList());
        wareSkuLockVo.setLocks(collect);
        return wareSkuLockVo;
    }

    /**
     * 保存订单
     *
     * @param orderTo orderTo
     */
    private void saveOrder(CreateOrderTo orderTo) {
        //1.保存id
        OrderEntity order = orderTo.getOrder();
        order.setModifyTime(new Date());
        this.save(order);
        //2.保存订单项
        List<OrderItemEntity> orderItems = orderTo.getOrderItems();
        itemService.saveBatch(orderItems);
    }

    /**
     * 原子校验令牌
     *
     * @param vo       vo
     * @param memberId 用户id
     * @return 0/1状态
     */
    private Long atomTokenCheck(OrderSubmitVo vo, Long memberId) {
        String tokenKey = OrderConstant.USER_ORDER_TOKEN_PRE + memberId;
        String orderToken = vo.getOrderToken();
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 " +
                "end";
        return redisUtil.executeScript(script, tokenKey, orderToken);
    }

    /**
     * 创建订单to
     *
     * @param vo vo
     * @return 实体
     */
    private CreateOrderTo createOrder(OrderSubmitVo vo) {
        CreateOrderTo orderTo = new CreateOrderTo();
        //1.创建订单实体
        String orderSn = IdWorker.getTimeId();
        OrderEntity orderEntity = builderOrderEntity(vo, orderSn);
        orderTo.setOrder(orderEntity);
        //2. 获取所有订单项
        List<OrderItemEntity> orderItemEntities = builderOrderItems(orderSn);
        orderTo.setOrderItems(orderItemEntities);
        //3.计算价格
        computePrice(orderEntity, orderItemEntities);
        return orderTo;
    }

    /**
     * 比价
     *
     * @param orderEntity       订单
     * @param orderItemEntities 订单项
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
//        BigDecimal totalAmount = orderItemEntities.stream()
//                .map(OrderItemEntity::getRealAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal promotion = BigDecimal.ZERO;
        BigDecimal couponAmount = BigDecimal.ZERO;
        BigDecimal integrationAmount = BigDecimal.ZERO;
        Integer giftGrowth = 0;
        Integer integration = 0;
        for (OrderItemEntity itemEntity : orderItemEntities) {
            totalAmount = totalAmount.add(itemEntity.getRealAmount());
            promotion = promotion.add(itemEntity.getPromotionAmount());
            couponAmount = couponAmount.add(itemEntity.getCouponAmount());
            integrationAmount = integrationAmount.add(itemEntity.getIntegrationAmount());
            giftGrowth += itemEntity.getGiftGrowth();
            integration += itemEntity.getGiftIntegration();
        }
        orderEntity.setPayAmount(totalAmount.add(orderEntity.getFreightAmount()));
        orderEntity.setTotalAmount(totalAmount);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setCouponAmount(couponAmount);
        orderEntity.setIntegrationAmount(integrationAmount);
        orderEntity.setGrowth(giftGrowth);
        orderEntity.setIntegration(integration);
    }

    /**
     * 获取订单项集合
     *
     * @param orderSn 订单编号
     * @return 集合
     */
    private List<OrderItemEntity> builderOrderItems(String orderSn) {
        ResponseVo<List<CartItem>> cartItemsRes = cartFeignService.getUserCartItems();
        if (cartItemsRes == null || cartItemsRes.getCode() != 0) {
            log.error("[cartFeignService] is error res:{}", cartItemsRes);
            ResponseHelper.execption(12001, "调用服务异常");
        }
        return cartItemsRes.getData().stream().map(item -> builderOrderItem(item, orderSn)).collect(Collectors.toList());
    }

    /**
     * 获取订单项
     *
     * @param item    item
     * @param orderSn 订单号
     * @return 订单项实体
     */
    private OrderItemEntity builderOrderItem(CartItem item, String orderSn) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        ResponseVo<SpuInfoEntity> spuInfoRes = productFeignService.getSpuInfoBySkuId(item.getSkuId());
        if (spuInfoRes == null || spuInfoRes.getCode() != 0) {
            log.error("[productFeignService] getSpuInfoBySkuId is error res={}", spuInfoRes);
            ResponseHelper.execption(12001, "调用服务异常");
        }
        //订单号
        itemEntity.setOrderSn(orderSn);
        // sku信息
        Integer gift = item.getPrice().multiply(new BigDecimal(item.getCount().toString())).intValue();
        itemEntity.setSkuId(item.getSkuId()).setSkuName(item.getTitle()).setSkuPic(item.getImage())
                .setSkuPrice(item.getPrice()).setSkuQuantity(item.getCount())
                .setSkuAttrsVals(StringUtils.collectionToDelimitedString(item.getSkuAttr(), ";"))
                .setGiftGrowth(gift).setGiftIntegration(gift);
        // spu信息
        SpuInfoEntity spuInfoEntity = spuInfoRes.getData();
        itemEntity.setSpuId(spuInfoEntity.getId()).setSpuBrand(spuInfoEntity.getBrandId().toString())
                .setSpuName(spuInfoEntity.getSpuName()).setCategoryId(spuInfoEntity.getCatalogId());
        // 设置加个 默认给0
        itemEntity.setPromotionAmount(BigDecimal.ZERO);
        itemEntity.setCouponAmount(BigDecimal.ZERO);
        itemEntity.setIntegrationAmount(BigDecimal.ZERO);
        // 实际金额
        BigDecimal price = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        price = price.subtract(itemEntity.getPromotionAmount()).subtract(itemEntity.getCouponAmount())
                .subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(price);
        return itemEntity;
    }

    /**
     * 构建订单实体
     *
     * @param vo      vo
     * @param orderSn 订单号
     * @return 订单实体
     */
    private OrderEntity builderOrderEntity(OrderSubmitVo vo, String orderSn) {
        OrderEntity orderEntity = new OrderEntity();
        ResponseVo<FareVo> fareRes = wareFeignService.getFare(vo.getAddrId());
        if (fareRes == null || fareRes.getCode() != 0) {
            log.error("[wareFeignService] is error res:{}", fareRes);
            ResponseHelper.execption(12001, "调用服务异常");
        }
        FareVo fareVo = fareRes.getData();
        MemberRespVo memberRespVo = LoginInterceptor.threadLocal.get();
        orderEntity
                .setOrderSn(orderSn).setFreightAmount(fareVo.getFare())
                .setReceiverCity(fareVo.getAddress().getCity())
                .setReceiverDetailAddress(fareVo.getAddress().getDetailAddress())
                .setReceiverName(fareVo.getAddress().getName()).setReceiverPhone(fareVo.getAddress().getPhone())
                .setReceiverPostCode(fareVo.getAddress().getPostCode())
                .setReceiverProvince(fareVo.getAddress().getProvince())
                .setReceiverRegion(fareVo.getAddress().getRegion()).setDeleteStatus(0)
                .setStatus(OrderStatusEnum.CREATE_NEW.getCode()).setAutoConfirmDay(7)
                .setMemberId(memberRespVo.getId()).setMemberUsername(memberRespVo.getUsername());
        return orderEntity;
    }

    /**
     * 获取选中的购物项
     *
     * @return 购物项集合
     */
    private List<OrderItemVo> getOrderItemVos() {
        ResponseVo<List<CartItem>> cartResp = cartFeignService.getUserCartItems();
        if (cartResp == null || cartResp.getCode() != 0) {
            log.error("[confirmOrder] getUserCartItems is Exception res = {} ", cartResp);
//            ResponseHelper.execption(12001,"调用服务异常");
            return null;
        }
        return cartResp.getData().stream().map(item -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            BeanUtils.copyProperties(item, orderItemVo);
            return orderItemVo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取用户地址列表
     *
     * @param memberRespVo 用户信息
     * @return 地址列表
     */
    private List<MemberAddressVo> getMemberAddressVos(MemberRespVo memberRespVo) {
        ResponseVo<List<MemberReceiveAddressEntity>> addressResp = memberFeignService.getAddress(memberRespVo.getId());
        if (addressResp == null || addressResp.getCode() != 0) {
            log.error("[confirmOrder] getAddress is Exception res={}", addressResp);
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