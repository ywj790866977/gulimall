package com.yanlaoge.gulimall.product.feign;


import com.yanlaoge.common.to.SkuReductionTo;
import com.yanlaoge.common.to.SpuBoundsTo;
import com.yanlaoge.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {

	@PostMapping("coupon/spubounds/save")
	R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

	@PostMapping("coupon/skufullreduction/saveInfo")
	R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
