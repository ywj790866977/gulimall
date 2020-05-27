package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.SpuInfoDescEntity;
import com.yanlaoge.gulimall.product.entity.SpuInfoEntity;

import com.yanlaoge.gulimall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存spu
     *
     * @param saveVo 保存vo
     */
    void saveSpuInfo(SpuSaveVo saveVo);

    /**
     * 分页条件查询
     *
     * @param params 分页信息
     * @return page
     */
    PageUtils queryPageByCondtion(Map<String, Object> params);

    /**
     * 保存spuinfo
     *
     * @param spuInfoEntity spuinfo实体
     */
    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    /**
     * 商品上架
     *
     * @param spuId 商品id
     */
    void up(Long spuId);
}

