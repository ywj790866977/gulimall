package com.yanlaoge.gulimall.product.dao;

import com.yanlaoge.gulimall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {
    /**
     * updateByCategoryId
     *
     * @param catId   carId
     * @param catName 名称
     */
    void updateByCategoryId(@Param("catId") Long catId, @Param("catName") String catName);
}
