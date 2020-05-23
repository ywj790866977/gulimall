package com.yanlaoge.gulimall.ware.feign;

import com.yanlaoge.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 好人
 * @date 2020-05-22 13:50
 **/
@FeignClient("guliamll-product")
public interface ProductFeignService {
    /**
     * 获取skuinfo
     *
     * @param skuId skuid
     * @return R
     */
    @RequestMapping("api/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);
}
