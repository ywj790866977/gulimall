package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.SpuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 保存spu图片
	 *
	 * @param id     spuid
	 * @param images 图片
	 */
	void saveSpuImages(Long id, List<String> images);
}

