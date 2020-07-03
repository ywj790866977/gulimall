package com.yanlaoge.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;
import com.yanlaoge.gulimall.product.dao.AttrGroupDao;
import com.yanlaoge.gulimall.product.entity.AttrEntity;
import com.yanlaoge.gulimall.product.entity.AttrGroupEntity;
import com.yanlaoge.gulimall.product.service.AttrGroupService;
import com.yanlaoge.gulimall.product.service.AttrService;
import com.yanlaoge.gulimall.product.vo.AttrGroupWithAttrsVo;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.yanlaoge.gulimall.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author rubyle
 * @date 2020/07/04
 */
@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

	@Resource
	private AttrService attrService;

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

	@Override
	public List<AttrGroupWithAttrsVo> getAttrGroupwithAttrs(Long catelogId) {
		//1.查询分组
		List<AttrGroupEntity> attrGroupEntities = list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id",
				catelogId));
		return attrGroupEntities.stream().map(item -> {
			AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
			BeanUtils.copyProperties(item, attrsVo);
			//2.查询所有属性
			List<AttrEntity> relationAttr = attrService.getRelationAttr(attrsVo.getAttrGroupId());
			attrsVo.setAttrs(relationAttr);
			return attrsVo;
		}).collect(Collectors.toList());

	}

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupwithSpuId( Long spuId,Long catalogId) {
        // 所有分组信息和对应的值
		return baseMapper.getAttrGroupwithSpuId(spuId,catalogId);
    }

}