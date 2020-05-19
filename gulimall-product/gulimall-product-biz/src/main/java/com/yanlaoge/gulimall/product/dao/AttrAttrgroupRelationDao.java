package com.yanlaoge.gulimall.product.dao;

import com.yanlaoge.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 属性&属性分组关联
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

	/**
	 * 批量删除
	 *
	 * @param attrAttrgroupRelationEntities 实体
	 */
	void deleteBatchRelation(@Param("entities") List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities);
}
