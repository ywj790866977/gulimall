package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return page
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据skuID查询图片
     *
     * @param skuId skuid
     * @return list
     */
    List<SkuImagesEntity> getImagesBySkuId(Long skuId);
}

