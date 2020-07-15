package com.yanlaoge.gulimall.seckill.feign;

import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.seckill.feign.fallback.SeckillFeignServiceFallBack;
import com.yanlaoge.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 好人
 * @date 2020-07-11 12:11
 **/
@FeignClient(value = "gulimall-seckill",fallback = SeckillFeignServiceFallBack.class)
public interface SeckillFeignService {
    /**
     * 获取秒杀商品详情
     *
     * @param skuId skuid
     * @return R
     */
    @GetMapping("/sku/seckill/{skuId}")
    ResponseVo<SeckillSkuRedisTo> getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
