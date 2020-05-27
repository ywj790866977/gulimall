package com.yanlaoge.gulimall.product.dao;

import com.yanlaoge.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> selectSearchAttr(@Param("attrIds") List<Long> attrIds);
}
