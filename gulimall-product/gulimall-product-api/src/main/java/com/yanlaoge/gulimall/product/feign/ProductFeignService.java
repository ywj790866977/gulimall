package com.yanlaoge.gulimall.product.feign;

import com.yanlaoge.common.utils.R;
import com.yanlaoge.gulimall.product.entity.SpuInfoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yanlaoge.common.utils.R;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.search.model.BrandEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
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

    /**
     * 获取skuinfo
     *
     * @param skuId skuid
     * @return R
     */
    @RequestMapping("product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);

    /**
     * 获取sku销售属性
     *
     * @param skuId skuId
     * @return R
     */
    @GetMapping("product/skusaleattrvalue/stringList/{skuId}")
    ResponseVo<List<String>> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);

    /**
     * 查询sku价格
     *
     * @param skuId skuid
     * @return 金额
     */
    @GetMapping("product/skuinfo/{skuId}/price")
    ResponseVo<BigDecimal> getSkuPrice(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId查询spu信息
     *
     * @param id sku信息
     * @return R
     */
    @GetMapping("product/spuinfo/skuId/{id}")
    ResponseVo<SpuInfoEntity> getSpuInfoBySkuId(@PathVariable("id") Long id);
}
