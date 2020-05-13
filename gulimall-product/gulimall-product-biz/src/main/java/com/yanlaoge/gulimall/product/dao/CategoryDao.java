package com.yanlaoge.gulimall.product.dao;

import com.yanlaoge.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
