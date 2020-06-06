package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.SkuInfoEntity;
import com.yanlaoge.gulimall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 条件查询
     *
     * @param params 查询条件
     * @return page
     */
    PageUtils queryPageByCondtion(Map<String, Object> params);

    /**
     * 保存 sku
     *
     * @param skuInfoEntity 实体
     */
    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    /**
     * 查询sks
     *
     * @param spuId spuid
     * @return list
     */
    List<SkuInfoEntity> getSkuByspuId(Long spuId);

    /**
     * item
     *
     * @param skuId skuid
     * @return vo
     */
    SkuItemVo item(Long skuId);
}

