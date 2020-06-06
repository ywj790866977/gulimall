package com.yanlaoge.gulimall.product.vo;

import com.yanlaoge.gulimall.product.entity.SkuImagesEntity;
import com.yanlaoge.gulimall.product.entity.SkuInfoEntity;
import com.yanlaoge.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.lang.ref.PhantomReference;
import java.util.List;

/**
 * @author 好人
 * @date 2020-06-06 08:17
 **/
@Data
public class SkuItemVo {
    /**
     * sku基本信息
     */
    private SkuInfoEntity info;
    /**
     * 图片信息
     */
    private List<SkuImagesEntity> images;
    /**
     * spu介绍
     */
    private SpuInfoDescEntity desp;
    /**
     * 销售属性
     */
    private List<SkuItemSaleAttrVo> saleAttr;
    /**
     * 规格参数
     */
    private List<SpuItemAttrGroupVo> groupAttrs;


}
