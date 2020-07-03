package com.yanlaoge.gulimall.ware.dao;

import com.yanlaoge.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    Long getSkuStock(@Param("skuId") Long skuId);

    /**
     * 查询sku包含的仓库
     *
     * @param skuId skuid
     * @return 仓库id
     */
    List<Long> queryWaresBySku(@Param("skuId") Long skuId);

    /**
     * 锁定库存
     *
     * @param skuId  skuId
     * @param wareId 仓库id
     * @param num    数量
     * @return 影响行数
     */
    Long lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);

    /**
     * '
     * 解锁库存
     *
     * @param skuId  skuId
     * @param wareId 仓库id
     * @param num    数量
     */
    void unLockStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);
}
