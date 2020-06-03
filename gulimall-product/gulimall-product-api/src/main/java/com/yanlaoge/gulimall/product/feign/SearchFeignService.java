package com.yanlaoge.gulimall.product.feign;

import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.search.model.SkuModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 检索服务
 *
 * @author rubyle
 */
@FeignClient("gulimall-search")
public interface SearchFeignService {
    /**
     * 保存商品
     *
     * @param skuModelList model
     * @return R
     */
    @PostMapping("/search/save/product")
    ResponseVo<Void> productStatusUp(@RequestBody List<SkuModel> skuModelList);
}
