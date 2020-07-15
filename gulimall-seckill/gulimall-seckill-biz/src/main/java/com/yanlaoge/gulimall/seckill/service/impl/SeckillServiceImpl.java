package com.yanlaoge.gulimall.seckill.service.impl;

import cn.hutool.core.bean.copier.BeanCopier;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.google.common.collect.Lists;
import com.yanlaoge.common.utils.*;
import com.yanlaoge.common.vo.MemberRespVo;
import com.yanlaoge.gulimall.coupon.entity.SeckillSessionEntity;
import com.yanlaoge.gulimall.coupon.feign.CouponFeignService;
import com.yanlaoge.gulimall.product.feign.ProductFeignService;
import com.yanlaoge.gulimall.seckill.constant.SeckillConstant;
import com.yanlaoge.gulimall.seckill.constant.SeckillResStatus;
import com.yanlaoge.gulimall.seckill.interceptor.LoginInterceptor;
import com.yanlaoge.gulimall.seckill.service.SeckillService;
import com.yanlaoge.gulimall.seckill.to.SeckillOrderTo;
import com.yanlaoge.gulimall.seckill.to.SeckillSkuRedisTo;
import com.yanlaoge.gulimall.seckill.vo.SkuInfoVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 好人
 * @date 2020-07-08 20:16
 **/
@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {

    @Resource
    private CouponFeignService couponFeignService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ProductFeignService productFeignService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void uploadSeckillSkuLatest3Days() {
        //1. 扫描参与活动的
        ResponseVo<List<SeckillSessionEntity>> responseVo = couponFeignService.getLates3DaysSession();
        if (responseVo == null || responseVo.getCode() != 0) {
            log.error("[couponFeignService] remote feign is err , res: {} ", responseVo);
            ResponseHelper.execption(SeckillResStatus.REMOTE_ERR);
        }
        List<SeckillSessionEntity> seckillSessionEntityList = responseVo.getData();
        //2.缓存到redis
        //2.1活动信息
        saveSessionInfos(seckillSessionEntityList);
        //2.2商品信息
        saveSessionSkuInfos(seckillSessionEntityList);
        //TODO 上架的数据需要设置过期时间
    }

    /**
     * 限流方法
     * 返回值需要一致,并且在同一个类里
     *
     * @return 秒杀to
     */
    public List<SeckillSkuRedisTo> blockHandler(BlockException e) {
        log.error("[blockHandler]  getCurrentSeckillSkus  is flow limit");
        return null;
    }

    /**
     * 限流方式
     * 1. try catch
     * try (Entry entry = SphU.entry("seckillSkus")) {}  catch (BlockException e){}
     * 2. 注解
     *
     * @return 秒杀to
     * @SentinelResource(value = "getCurrentSeckillSkus",blockHandler = "blockHandler")
     * value : 资源名
     * blockHandler : 限流后执行方法
     */
    @SentinelResource(value = "getCurrentSeckillSkus", blockHandler = "blockHandler")
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        //自定义限流资源
        try (Entry entry = SphU.entry("seckillSkus")) {
            // 当前时间
            long time = System.currentTimeMillis();
            Set<Object> keys = redisUtil.keys(SeckillConstant.SESSIONS_CACHE_PREFIX + "*");
            ServiceAssert.isNull(keys, SeckillResStatus.SECKILL_NULL);
            for (Object key : keys) {
                String key1 = (String) key;
                String[] strings = key1.replace(SeckillConstant.SESSIONS_CACHE_PREFIX, "").split("_");
                long start = Long.parseLong(strings[0]);
                long end = Long.parseLong(strings[1]);
                if (time >= start && time <= end) {
                    List<Object> all = redisUtil.lGet(key1, -100, 100);
                    //TODO string转换问题
                    List<Object> list = Lists.newArrayList();
                    for (Object o : all) {
                        List<Object> skus = (List<Object>) o;
                        List<Object> objects = redisUtil.hGet(SeckillConstant.SKUSKILL_CACHE_PREFIX, skus);
                        list.addAll(objects);
                    }
                    if (!CollectionUtils.isEmpty(list)) {
                        List<SeckillSkuRedisTo> collect1 =
                                list.stream().map(item1 -> JSON.parseObject((String) item1,
                                        SeckillSkuRedisTo.class)).collect(Collectors.toList());
                        return collect1;
                    }
                }
            }
        } catch (BlockException e) {
            log.error(" flow limit for source msg : [{}]", e.getMessage(), e);
        }

        // 获取
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {

        //找到所以参与需要秒杀的商品的key信息
        Set<Object> keys = redisUtil.hKeys(SeckillConstant.SKUSKILL_CACHE_PREFIX);
        if (CollectionUtils.isEmpty(keys)) {
            return null;
        }
        String regx = "\\d_" + skuId;
        for (Object key : keys) {
            String key1 = (String) key;
            if (Pattern.matches(regx, key1)) {
                String json = (String) redisUtil.hGet(SeckillConstant.SKUSKILL_CACHE_PREFIX, key1);
                SeckillSkuRedisTo redisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
                long now = System.currentTimeMillis();
                if (now >= redisTo.getStartTime() && now <= redisTo.getEndTime()) {
                    return redisTo;
                }
                redisTo.setRandmoCode(null);
                return redisTo;
            }
        }
        return null;
    }

    @SneakyThrows
    @Override
    public String kill(String killId, String key, Integer num) {
        //1.取出数据
        String json = (String) redisUtil.hGet(SeckillConstant.SKUSKILL_CACHE_PREFIX, killId);
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        SeckillSkuRedisTo skuRedisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
        //2.校验时间参数
        long now = System.currentTimeMillis();
        Long startTime = skuRedisTo.getStartTime();
        Long endTime = skuRedisTo.getEndTime();
        if (now < startTime || now > endTime) {
            return null;
        }
        //3.校验随机码
        String randmoCode = skuRedisTo.getRandmoCode();
        String redisId = skuRedisTo.getPromotionSessionId() + "_" + skuRedisTo.getSkuId();
        if (!randmoCode.equalsIgnoreCase(key) || !redisId.equals(killId)) {
            return null;
        }
        //4.验证购物数量
        Integer seckillLimit = skuRedisTo.getSeckillLimit();
        if (num > seckillLimit) {
            return null;
        }
        //5.是否已经购买过 '幂等性'
        MemberRespVo memberRespVo = LoginInterceptor.threadLocal.get();
        String isShopKey = memberRespVo.getId() + "_" + redisId;
        boolean isShop = redisUtil.setIfAbsent(isShopKey, num, endTime - startTime, TimeUnit.MILLISECONDS);
        if (!isShop) {
            return null;
        }
        //6.减库存
        RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SKU_STOCK_SEMAPHORE + randmoCode);
        boolean tryAcquire = semaphore.tryAcquire(num);
        if (!tryAcquire) {
            return null;
        }
        //7.快速下单
        String orderSn = IdWorker.getTimeId();
        //8.发送消息
        SeckillOrderTo seckillOrderTo = new SeckillOrderTo();
        seckillOrderTo.setMemberId(memberRespVo.getId());
        seckillOrderTo.setOrderSn(orderSn);
        seckillOrderTo.setNum(num);
        seckillOrderTo.setPromotionSessionId(skuRedisTo.getPromotionSessionId());
        seckillOrderTo.setSkuId(skuRedisTo.getSkuId());
        seckillOrderTo.setSeckillPrice(skuRedisTo.getSeckillPrice());

        rabbitTemplate.convertAndSend(SeckillConstant.ORDER_EVENT_EXCHANGE, SeckillConstant.ORDER_SECKILL_ROUTING_KEY,
                seckillOrderTo);

        return orderSn;
    }

    private void saveSessionSkuInfos(List<SeckillSessionEntity> seckillSessionEntityList) {
        if (!CollectionUtils.isEmpty(seckillSessionEntityList)) {
            seckillSessionEntityList.forEach(session -> session.getRelationSkus().forEach(item -> {
                String key = item.getPromotionSessionId() + "_" + item.getSkuId();
                boolean hHasKey = redisUtil.hHasKey(SeckillConstant.SKUSKILL_CACHE_PREFIX, key);
                if (!hHasKey) {
                    SeckillSkuRedisTo skuRedisTo = new SeckillSkuRedisTo();
                    BeanUtils.copyProperties(item, skuRedisTo);
                    //查询sku基本数据
                    R info = productFeignService.info(item.getSkuId());
                    SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    skuRedisTo.setSkuInfo(skuInfo);
                    //随机码
                    String randomCode = UUID.randomUUID().toString().replaceAll("-", "");
                    skuRedisTo.setRandmoCode(randomCode);
                    //设置时间
                    skuRedisTo.setStartTime(session.getStartTime().getTime());
                    //保存
                    skuRedisTo.setEndTime(session.getEndTime().getTime());
                    redisUtil.hSet(SeckillConstant.SKUSKILL_CACHE_PREFIX, key, JSON.toJSONString(skuRedisTo));
                    //引入分布式的信号量 限流
                    RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SKU_STOCK_SEMAPHORE + randomCode);
                    //商品秒杀得数量作为信号量
                    semaphore.trySetPermits(item.getSeckillCount());
                }

            }));
        }
    }

    private void saveSessionInfos(List<SeckillSessionEntity> seckillSessionEntityList) {
        if (!CollectionUtils.isEmpty(seckillSessionEntityList)) {
            seckillSessionEntityList.forEach(session -> {
                long start = session.getStartTime().getTime();
                long end = session.getEndTime().getTime();
                String key = SeckillConstant.SESSIONS_CACHE_PREFIX + start + "_" + end;
                boolean key1 = redisUtil.hasKey(key);
                if (!key1) {
                    List<String> skuIds =
                            session.getRelationSkus().stream().map(item -> item.getPromotionSessionId() + "_" + item.getSkuId()).collect(Collectors.toList());
                    redisUtil.llSet(key, skuIds);
                }
            });
        }
    }


}
