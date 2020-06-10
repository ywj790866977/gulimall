package com.yanlaoge.gulimall.product.dao;

import com.yanlaoge.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanlaoge.gulimall.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    /**
     * 查询销售属性
     *
     * @param spuId spuid
     * @return 集合
     */
    List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(@Param("spuId") Long spuId);

    /**
     * getSaleAttrsBySkuId
     *
     * @param skuId skuid
     * @return 集合
     */
    List<String> getSaleAttrsBySkuId(@Param("skuId") Long skuId);
}
