package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.yanlaoge.gulimall.product.vo.SkuItemSaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return pag
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询销售属性
     *
     * @param spuId spuid
     * @return 集合
     */
    List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId);

    /**
     * 根据skuid查询属性
     *
     * @param skuId skuid
     * @return 集合
     */
    List<String> getSaleAttrsBySkuId(Long skuId);
}

