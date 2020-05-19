package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 保存详细信息
	 *
	 * @param categoryBrandRelation 实体
	 */
	void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

	/**
	 * 更新 品牌 名字
	 *
	 * @param brandId 品牌id
	 * @param name    品牌名
	 */
	void updateByBrandId(Long brandId, String name);

	/**
	 * 更新分类
	 *
	 * @param catId 分类id
	 * @param name  名字
	 */
	void updateByCategoryId(Long catId, String name);
}

