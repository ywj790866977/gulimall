package com.yanlaoge.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;
import com.yanlaoge.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.yanlaoge.gulimall.product.dao.AttrDao;
import com.yanlaoge.gulimall.product.dao.AttrGroupDao;
import com.yanlaoge.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.yanlaoge.gulimall.product.entity.AttrEntity;
import com.yanlaoge.gulimall.product.entity.AttrGroupEntity;
import com.yanlaoge.gulimall.product.entity.CategoryEntity;
import com.yanlaoge.gulimall.product.enums.AttrTypeEnum;
import com.yanlaoge.gulimall.product.service.AttrAttrgroupRelationService;
import com.yanlaoge.gulimall.product.service.AttrGroupService;
import com.yanlaoge.gulimall.product.service.AttrService;
import com.yanlaoge.gulimall.product.service.CategoryService;
import com.yanlaoge.gulimall.product.vo.AttrGroupRelationVo;
import com.yanlaoge.gulimall.product.vo.AttrResVo;
import com.yanlaoge.gulimall.product.vo.AttrVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 参数service
 *
 * @author rubyle
 */
@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

	@Resource
	private AttrAttrgroupRelationService relationService;
	@Resource
	private CategoryService categoryService;
	@Resource
	private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
	@Resource
	private AttrGroupService attrGroupService;
	@Resource
	private AttrGroupDao attrGroupDao;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<AttrEntity> page = this.page(
				new Query<AttrEntity>().getPage(params),
				new QueryWrapper<AttrEntity>()
		);

		return new PageUtils(page);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveAttr(AttrVo attrVo) {
		AttrEntity attrEntity = new AttrEntity();
		BeanUtils.copyProperties(attrVo, attrEntity);
		this.save(attrEntity);
		if (AttrTypeEnum.BASE.getCode().equals(attrVo.getAttrType()) && attrVo.getAttrGroupId() != null) {
			AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
			entity.setAttrGroupId(attrVo.getAttrGroupId());
			entity.setAttrId(attrEntity.getAttrId());
			relationService.save(entity);
		}

	}

	@Override
	public PageUtils queryBaseAttrList(Map<String, Object> params, Long catelogId, String type) {
		Integer attrType = AttrTypeEnum.BASE.getType().equals(type) ? AttrTypeEnum.BASE.getCode() :
				AttrTypeEnum.SALE.getCode();
		QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("attr_type", attrType);
		if (catelogId != 0) {
			wrapper.eq("catelog_id", catelogId);
		}
		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			wrapper.and((o) -> {
				o.eq("attr_id", key).or().like("attr_name", key);
			});
		}
		IPage<AttrEntity> page = this.page(
				new Query<AttrEntity>().getPage(params),
				wrapper
		);
		List<AttrEntity> records = page.getRecords();
		List<AttrResVo> attrResVos = records.stream().map(record -> {
			// 转换bean
			AttrResVo attrResVo = new AttrResVo();
			BeanUtils.copyProperties(record, attrResVo);
			// 设置CatelogName
			CategoryEntity categoryEntity = categoryService.getById(attrResVo.getCatelogId());
			if (categoryEntity != null) {
				attrResVo.setCatelogName(categoryEntity.getName());
			}

			// 设置groupName
			// 1. 根据attrId 查询 关联表.
			if (AttrTypeEnum.BASE.getType().equals(type)) {
				AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao
						.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",
								attrResVo.getAttrId()));
				if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
					// 2. 根据关联表 查询 组
					AttrGroupEntity groupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
					attrResVo.setGroupName(groupEntity.getAttrGroupName());
				}
			}
			return attrResVo;
		}).collect(Collectors.toList());
		PageUtils pageUtils = new PageUtils(page);
		pageUtils.setList(attrResVos);
		return pageUtils;
	}

	@Override
	public AttrResVo getAttrInfo(Long attrId) {
		AttrEntity attrEntity = getById(attrId);
		AttrResVo attrResVo = new AttrResVo();
		BeanUtils.copyProperties(attrEntity, attrResVo);
		// 设置分组信息
		if (AttrTypeEnum.BASE.getCode().equals(attrResVo.getAttrType())) {
			AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao
					.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
			if (relationEntity != null) {
				attrResVo.setAttrGroupId(relationEntity.getAttrGroupId());
				AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
				attrResVo.setGroupName(attrGroupEntity.getAttrGroupName());
			}
		}

		// 设置分类信息
		List<Long> catelogIds = categoryService.findCatelogIds(attrEntity.getCatelogId());
		attrResVo.setCatelogPath(catelogIds);
		CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
		attrResVo.setCatelogName(categoryEntity.getName());
		return attrResVo;
	}

	@Override
	public void updateAttr(AttrVo attr) {
		AttrEntity attrEntity = new AttrEntity();
		BeanUtils.copyProperties(attr, attrEntity);
		updateById(attrEntity);
		//设置关联关系
		if (AttrTypeEnum.BASE.getCode().equals(attrEntity.getAttrType())) {
			AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
			entity.setAttrId(attr.getAttrId());
			entity.setAttrGroupId(attr.getAttrGroupId());

			Integer count = attrAttrgroupRelationDao
					.selectCount(new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
			if (count > 0) {
				attrAttrgroupRelationDao.update(entity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",
						attr.getAttrId()));
			} else {
				relationService.save(entity);
			}
		}

	}

	@Override
	public List<AttrEntity> getRelationAttr(Long attrgroupId) {

		List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao
				.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
		List<Long> attrIds = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId)
				.collect(Collectors.toList());
		if(CollectionUtils.isEmpty(attrIds)){
			return Lists.newArrayList();
		}
		return listByIds(attrIds);
	}

	@Override
	public void deleteRelation(List<AttrGroupRelationVo> relationVos) {
		List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = relationVos.stream().map(relation -> {
			AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
			BeanUtils.copyProperties(relation, entity);
			return entity;
		}).collect(Collectors.toList());
		attrAttrgroupRelationDao.deleteBatchRelation(attrAttrgroupRelationEntities);
	}

	@Override
	public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
		// 当前分组查询所属分类的所有属性
		AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrgroupId);
		Long catelogId = attrGroupEntity.getCatelogId();
		// 当前分类只能关联别的分组没有关联的属性
		List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>()
				.eq("catelog_id", catelogId));
		List<Long> attrGroupIds = attrGroupEntities.stream().map(AttrGroupEntity::getAttrGroupId)
				.collect(Collectors.toList());
		//查询出已经关联的书信
		List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao.selectList(
				new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", attrGroupIds));
		//已经关联的属性
		List<Long> attrIds =
				relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
		//进行排除
		QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type",
				AttrTypeEnum.BASE.getCode());
		if(!CollectionUtils.isEmpty(attrIds)){
			wrapper.notIn("attr_id", attrIds);
		}
		String key = (String) params.get("key");
		if(!StringUtils.isEmpty(key)){
			wrapper.and(o->{
				o.eq("attr_id",key).or().like("attr_name",key);
			});
		}
		IPage<AttrEntity> page = page(new Query<AttrEntity>().getPage(params), wrapper);
		return new PageUtils(page);
	}

    @Override
    public List<Long> selectSearchAttr(List<Long> attrIds) {
		return baseMapper.selectSearchAttr(attrIds);
//		List<AttrEntity> attrEntities = listByIds(attrIds);
//		return attrEntities.stream()
//				.filter(item -> StaticConstant.ONE.equals(item.getSearchType()))
//				.map(AttrEntity::getAttrId).collect(Collectors.toList());
	}

}