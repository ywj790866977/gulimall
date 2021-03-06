package com.yanlaoge.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yanlaoge.gulimall.product.dao.BrandDao;
import com.yanlaoge.gulimall.product.dao.CategoryDao;
import com.yanlaoge.gulimall.product.entity.BrandEntity;
import com.yanlaoge.gulimall.product.entity.CategoryEntity;
import com.yanlaoge.gulimall.product.service.BrandService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;

import com.yanlaoge.gulimall.product.dao.CategoryBrandRelationDao;
import com.yanlaoge.gulimall.product.entity.CategoryBrandRelationEntity;
import com.yanlaoge.gulimall.product.service.CategoryBrandRelationService;

import javax.annotation.Resource;

/**
 * @author rubyle
 * @date 2020/07/04
 */
@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends
		ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

	@Resource
	private BrandDao brandDao;
	@Resource
	private CategoryDao categoryDao;
	@Resource
	private BrandService brandService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<CategoryBrandRelationEntity> page = this.page(
				new Query<CategoryBrandRelationEntity>().getPage(params),
				new QueryWrapper<CategoryBrandRelationEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
		BrandEntity brandEntity = brandDao.selectById(categoryBrandRelation.getBrandId());
		CategoryEntity categoryEntity = categoryDao.selectById(categoryBrandRelation.getCatelogId());
		categoryBrandRelation.setBrandName(brandEntity.getName());
		categoryBrandRelation.setCatelogName(categoryEntity.getName());
		save(categoryBrandRelation);
	}

	@Override
	public void updateByBrandId(Long brandId, String brandName) {
		UpdateWrapper<CategoryBrandRelationEntity> wrapper = new UpdateWrapper<>();
		wrapper.eq("brand_id", brandId);
		CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
		entity.setBrandId(brandId);
		entity.setBrandName(brandName);
		update(entity, wrapper);
	}

	@Override
	public void updateByCategoryId(Long catId, String catName) {
		baseMapper.updateByCategoryId(catId, catName);
	}

	@Override
	public List<BrandEntity> getBrandsByCatId(Long catId) {
		List<CategoryBrandRelationEntity> relationEntities = baseMapper
				.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
		List<Long> brandIds = relationEntities.stream().map(CategoryBrandRelationEntity::getBrandId)
				.collect(Collectors.toList());
		return brandService.listByIds(brandIds);
//		return relationEntities.stream().map(item -> brandService.getById(item.getBrandId()))
//				.collect(Collectors.toList());
	}

}