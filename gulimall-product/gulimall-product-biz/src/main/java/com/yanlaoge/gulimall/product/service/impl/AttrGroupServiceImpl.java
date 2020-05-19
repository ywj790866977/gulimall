package com.yanlaoge.gulimall.product.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;

import com.yanlaoge.gulimall.product.dao.AttrGroupDao;
import com.yanlaoge.gulimall.product.entity.AttrGroupEntity;
import com.yanlaoge.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<AttrGroupEntity> page = this.page(
				new Query<AttrGroupEntity>().getPage(params),
				new QueryWrapper<AttrGroupEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
		QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			wrapper.and((o) -> {
				o.eq("attr_group_id", key).or().like("attr_group_name", key);
			});
		}
		if (catelogId == 0) {
			IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
			return new PageUtils(page);
		} else {

			IPage<AttrGroupEntity> page = this
					.page(new Query<AttrGroupEntity>().getPage(params), wrapper.eq("catelog_id", catelogId));
			return new PageUtils(page);
		}
	}

}