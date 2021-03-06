package com.yanlaoge.gulimall.coupon.feign;

import com.yanlaoge.common.to.SkuReductionTo;
import com.yanlaoge.common.to.SpuBoundsTo;
import com.yanlaoge.common.utils.R;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.coupon.entity.SeckillSessionEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author runyle
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    /**
     * 保存spuBound
     *
     * @param spuBoundsTo to
     * @return R
     */
    @PostMapping("coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    /**
     * 保存SkuReduction
     *
     * @param skuReductionTo to
     * @return R
     */
    @PostMapping("coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);

    /**
     * 查询近3天需要上架的商品
     *
     * @return R
     */
    @GetMapping("coupon/seckillsession/lates3DaysSession")
    ResponseVo<List<SeckillSessionEntity>> getLates3DaysSession();
}
