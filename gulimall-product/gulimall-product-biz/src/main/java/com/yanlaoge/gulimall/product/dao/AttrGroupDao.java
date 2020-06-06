package com.yanlaoge.gulimall.product.dao;

import com.yanlaoge.gulimall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanlaoge.gulimall.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    /**
     * 查询属性
     *
     * @param spuId     spuid
     * @param catalogId 分类id
     * @return 集合
     */
    List<SpuItemAttrGroupVo> getAttrGroupwithSpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);

}
