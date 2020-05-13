package com.yanlaoge.gulimall.coupon.feign;

import com.yanlaoge.common.utils.R;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 优惠券
 *
 * @author rebyle
 */

@FeignClient("gulimall-coupon")
public interface CouponApi {

    /**
     * list
     *
     * @param params 参数
     * @return R
     */
    @RequestMapping("/coupon/coupon/list")
    R list(@RequestParam Map<String, Object> params);

    /**
     * test
     *
     * @return R
     */
    @RequestMapping("/coupon/coupon/test")
    R test();
}
