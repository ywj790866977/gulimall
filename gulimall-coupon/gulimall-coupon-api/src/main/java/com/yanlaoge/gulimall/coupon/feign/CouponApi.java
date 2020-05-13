package com.yanlaoge.gulimall.coupon.feign;

import com.yanlaoge.common.utils.R;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	@GetMapping("/coupon/coupon/list")
	R list(@RequestParam Map<String, Object> params);
}
