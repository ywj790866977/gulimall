package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

	/**
	 * 查询分级结构
	 * @return
	 */
	List<CategoryEntity> listByTree();
}

