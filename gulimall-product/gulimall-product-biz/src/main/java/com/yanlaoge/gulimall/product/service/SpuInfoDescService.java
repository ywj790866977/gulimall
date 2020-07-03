package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {
	/**
	 * 分页查询
	 *
	 * @param params 分页参数
	 * @return 分页
	 */
    PageUtils queryPage(Map<String, Object> params);

	/**
	 * 保存spuDesc
	 *
	 * @param spuInfoDescEntity 保存实体
	 */
	void saveSpuDescript(SpuInfoDescEntity spuInfoDescEntity);
}

