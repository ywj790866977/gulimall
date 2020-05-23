package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存信息
     *
     * @param entities 实体
     */
    void saveProductAttr(List<ProductAttrValueEntity> entities);

    /**
     * 属性集合
     *
     * @param spuId spuid
     * @return 集合
     */
    List<ProductAttrValueEntity> baseAttrListforspu(Long spuId);

    /**
     * 更新
     *
     * @param spuId    spuid
     * @param entities 实体集合
     */
    void updateBySpuid(Long spuId, List<ProductAttrValueEntity> entities);

}

