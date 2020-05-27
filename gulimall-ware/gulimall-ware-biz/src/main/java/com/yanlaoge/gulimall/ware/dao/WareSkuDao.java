package com.yanlaoge.gulimall.ware.dao;

import com.yanlaoge.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 *
 * @author rubyle
 * @email besokuser@163.com
 * @date 2020-05-13 15:47:12
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
    /**
     * 添加库存
     *
     * @param skuId  商品id
     * @param wareId 仓库id
     * @param skuNum 数量
     */
    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    /**
     * 查询库存
     *
     * @param skuId 商品id
     * @return 库存
     */
    Long getSkuStock(Long skuId);
}
