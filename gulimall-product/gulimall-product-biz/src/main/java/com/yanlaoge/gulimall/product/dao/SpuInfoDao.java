package com.yanlaoge.gulimall.product.dao;

import com.yanlaoge.gulimall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    /**
     * 修改商品状态
     *
     * @param spuId spuId
     * @param code  状态
     */
    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") Integer code);
}
