package com.yanlaoge.gulimall.search.feign;

import com.yanlaoge.common.utils.R;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.search.model.BrandEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 好人
 * @date 2020-06-05 09:50
 **/
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * 根据id查询属性
     *
     * @param attrId 属性id
     * @return R
     */
    @GetMapping("product/attr/info/{attrId}")
    R attrInfo(@PathVariable("attrId") Long attrId);

    /**
     * 获取品牌
     *
     * @param ids ids
     * @return R
     */
    @GetMapping("product/brand/infos")
    ResponseVo<List<BrandEntity>> infos(@RequestParam("brandIds") List<Long> ids);
}
