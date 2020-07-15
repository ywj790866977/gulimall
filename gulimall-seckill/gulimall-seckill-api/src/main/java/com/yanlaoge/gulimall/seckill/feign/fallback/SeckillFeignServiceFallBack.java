package com.yanlaoge.gulimall.seckill.feign.fallback;

import com.yanlaoge.common.utils.CommonStatusCode;
import com.yanlaoge.common.utils.ResponseHelper;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.seckill.feign.SeckillFeignService;
import com.yanlaoge.gulimall.seckill.to.SeckillSkuRedisTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 好人
 * @date 2020-07-12 10:39
 **/
@Slf4j
@Service
public class SeckillFeignServiceFallBack implements SeckillFeignService {
    @Override
    public ResponseVo<SeckillSkuRedisTo> getSkuSeckillInfo(Long skuId) {
        log.info("[SeckillFeignServiceFallBack] 调用熔断方法 getSkuSeckillInfo");
        return ResponseHelper.error(CommonStatusCode.TO_MANY_REQUEST);
    }
}
