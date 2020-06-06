package com.yanlaoge.gulimall.product.service.impl;

import java.math.BigDecimal;

import com.yanlaoge.gulimall.product.entity.SkuImagesEntity;
import com.yanlaoge.gulimall.product.entity.SpuInfoDescEntity;
import com.yanlaoge.gulimall.product.service.*;
import com.yanlaoge.gulimall.product.vo.SkuItemSaleAttrVo;
import com.yanlaoge.gulimall.product.vo.SkuItemVo;
import com.yanlaoge.gulimall.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;

import com.yanlaoge.gulimall.product.dao.SkuInfoDao;
import com.yanlaoge.gulimall.product.entity.SkuInfoEntity;

import javax.annotation.Resource;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

	@Resource
	private SkuImagesService skuImagesService;
	@Resource
	private SpuInfoDescService spuInfoDescService;
	@Resource
	private AttrGroupService attrGroupService;
	@Resource
	private SkuSaleAttrValueService skuSaleAttrValueService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SkuInfoEntity> page = this.page(
				new Query<SkuInfoEntity>().getPage(params),
				new QueryWrapper<SkuInfoEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPageByCondtion(Map<String, Object> params) {
		QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();

		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			wrapper.and(o -> {
				o.eq("sku_id", key).or().like("sku_name", key);
			});
		}
		String catalogId = (String) params.get("catelogId");
		if (!StringUtils.isEmpty(catalogId) && !"0".equalsIgnoreCase(catalogId)) {
			wrapper.eq("catalog_id", catalogId);
		}
		String brandId = (String) params.get("brandId");
		if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
			wrapper.eq("brand_id", brandId);
		}
		String min = (String) params.get("min");
		if (!StringUtils.isEmpty(min)) {
			wrapper.ge("price", min);
		}
		String max = (String) params.get("max");
		if (!StringUtils.isEmpty(max)) {
			BigDecimal maxPrice = new BigDecimal(max);
			if (BigDecimal.ZERO.compareTo(maxPrice) < 0) {
				wrapper.le("price", max);
			}
		}
		IPage<SkuInfoEntity> page = this.page(
				new Query<SkuInfoEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Override
	public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
		this.save(skuInfoEntity);
	}

    @Override
    public List<SkuInfoEntity> getSkuByspuId(Long spuId) {
		return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));
    }

    @Override
    public SkuItemVo item(Long skuId) {
		SkuItemVo skuItemVo = new SkuItemVo();
		// 1. sku基本信息
		SkuInfoEntity skuInfoEntity = getById(skuId);
		skuItemVo.setInfo(skuInfoEntity);
		Long catalogId = skuInfoEntity.getCatalogId();
		Long spuId = skuInfoEntity.getSpuId();
		// 2.图片信息
		List<SkuImagesEntity> images =  skuImagesService.getImagesBySkuId(skuId);
		skuItemVo.setImages(images);
		// 3. 销售属性组合
		List<SkuItemSaleAttrVo> skuItemSaleAttrVos =  skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
		skuItemVo.setSaleAttr(skuItemSaleAttrVos);
		// 4. 商品介绍
		SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
		skuItemVo.setDesp(spuInfoDescEntity);
		// 5. 规格属性
		List<SpuItemAttrGroupVo> attrGroupVos =  attrGroupService.getAttrGroupwithSpuId(spuId,catalogId);
		skuItemVo.setGroupAttrs(attrGroupVos);
		return  null;
    }

}