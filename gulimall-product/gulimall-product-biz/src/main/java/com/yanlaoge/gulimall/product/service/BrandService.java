package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
public interface BrandService extends IService<BrandEntity> {

	/**
	 * 分页查询
	 *
	 * @param params 分页参数
	 * @return Page
	 */
	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 更新(涉及出都更新)
	 *
	 * @param brand 实体
	 */
	void updateDetail(BrandEntity brand);

	/**
	 * 批量获取
	 * @param ids ids
	 * @return 集合
	 */
    List<BrandEntity> getBrandsByIds(List<Long> ids);
}

