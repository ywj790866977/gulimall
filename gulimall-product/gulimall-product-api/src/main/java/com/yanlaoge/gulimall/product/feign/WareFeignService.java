package com.yanlaoge.gulimall.product.feign;

import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.ware.vo.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author rubyle
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {
    /**
     * 查询库存
     *
     * @param skuIds 商品ids
     * @return R
     */
    @PostMapping("/ware/waresku/hasStock")
    ResponseVo<List<SkuHasStockVo>> getSkuHasStock(@RequestBody List<Long> skuIds);
}
