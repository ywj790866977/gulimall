package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.AttrAttrgroupRelationEntity;

import com.yanlaoge.gulimall.product.vo.AttrGroupRelationVo;
import com.yanlaoge.gulimall.product.vo.AttrGroupWithAttrsVo;
import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author rubyle
 * @email sunlightcs@gmail.com
 * @date 2020-05-13 14:21:04
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 保存关联关系
	 *
	 * @param relationVos 关系集合
	 */
	void saveRelation(List<AttrGroupRelationVo> relationVos);

}

