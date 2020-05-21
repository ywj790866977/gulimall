package com.yanlaoge.gulimall.product.service.impl;

import com.yanlaoge.common.to.SkuReductionTo;
import com.yanlaoge.common.utils.R;
import com.yanlaoge.common.to.SpuBoundsTo;
import com.yanlaoge.gulimall.product.entity.AttrEntity;
import com.yanlaoge.gulimall.product.entity.ProductAttrValueEntity;
import com.yanlaoge.gulimall.product.entity.SkuImagesEntity;
import com.yanlaoge.gulimall.product.entity.SkuInfoEntity;
import com.yanlaoge.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.yanlaoge.gulimall.product.entity.SpuInfoDescEntity;
import com.yanlaoge.gulimall.product.feign.CouponFeignService;
import com.yanlaoge.gulimall.product.service.AttrService;
import com.yanlaoge.gulimall.product.service.ProductAttrValueService;
import com.yanlaoge.gulimall.product.service.SkuImagesService;
import com.yanlaoge.gulimall.product.service.SkuInfoService;
import com.yanlaoge.gulimall.product.service.SkuSaleAttrValueService;
import com.yanlaoge.gulimall.product.service.SpuImagesService;
import com.yanlaoge.gulimall.product.service.SpuInfoDescService;
import com.yanlaoge.gulimall.product.vo.Attr;
import com.yanlaoge.gulimall.product.vo.BaseAttrs;
import com.yanlaoge.gulimall.product.vo.Bounds;
import com.yanlaoge.gulimall.product.vo.Images;
import com.yanlaoge.gulimall.product.vo.Skus;
import com.yanlaoge.gulimall.product.vo.SpuSaveVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;

import com.yanlaoge.gulimall.product.dao.SpuInfoDao;
import com.yanlaoge.gulimall.product.entity.SpuInfoEntity;
import com.yanlaoge.gulimall.product.service.SpuInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

	@Resource
	private SpuInfoDescService spuInfoDescService;
	@Resource
	private SpuImagesService spuImagesService;
	@Resource
	private AttrService attrService;
	@Resource
	private ProductAttrValueService productAttrValueService;
	@Resource
	private SkuInfoService skuInfoService;
	@Resource
	private SkuImagesService skuImagesService;
	@Resource
	private SkuSaleAttrValueService skuSaleAttrValueService;
	@Resource
	private CouponFeignService couponFeignService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SpuInfoEntity> page = this.page(
				new Query<SpuInfoEntity>().getPage(params),
				new QueryWrapper<SpuInfoEntity>()
		);

		return new PageUtils(page);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveSpuInfo(SpuSaveVo saveVo) {
		//1. 保存spu基本信息 pms_spu_info
		SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
		BeanUtils.copyProperties(saveVo, spuInfoEntity);
		spuInfoEntity.setCreateTime(new Date());
		spuInfoEntity.setUpdateTime(new Date());
		this.saveBaseSpuInfo(spuInfoEntity);

		//2. 保存spu描述图片 pms_spu_info_desc
		SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
		spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
		spuInfoDescEntity.setDecript(String.join(",", saveVo.getDecript()));
		spuInfoDescService.saveSpuDescript(spuInfoDescEntity);

		//3. 保存spu图片集 pms_spu_images
		List<String> images = saveVo.getImages();
		spuImagesService.saveSpuImages(spuInfoEntity.getId(), images);

		//4. 保存spu规格参数 pms_product_attr_value
		List<BaseAttrs> baseAttrs = saveVo.getBaseAttrs();
		List<ProductAttrValueEntity> entities = baseAttrs.stream().map(item -> {
			ProductAttrValueEntity entity = new ProductAttrValueEntity();
			entity.setAttrId(item.getAttrId());
			AttrEntity attrEntity = attrService.getById(item.getAttrId());
			entity.setAttrName(attrEntity.getAttrName());
			entity.setAttrValue(item.getAttrValues());
			entity.setQuickShow(item.getShowDesc());
			entity.setSpuId(spuInfoEntity.getId());
			return entity;
		}).collect(Collectors.toList());
		productAttrValueService.saveProductAttr(entities);

		//5. 保存 spu 积分信息 gulimall-sms -> sms_spu_bounds
		Bounds bounds = saveVo.getBounds();
		SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
		BeanUtils.copyProperties(bounds, spuBoundsTo);
		spuBoundsTo.setSpuId(spuInfoEntity.getId());
		R r = couponFeignService.saveSpuBounds(spuBoundsTo);
		Integer code = (Integer) r.get("code");
		if (!code.equals(0)) {
			log.error("【couponFeignService】 返回错误: {}", r);
		}
		//6. 保存sku信息
		List<Skus> skus = saveVo.getSkus();
		if (!CollectionUtils.isEmpty(skus)) {
			skus.forEach(sku -> {
				// 6.1 保存 sku 基本信息 pms_sku_info
				String defaultImg = "";
				for (Images image : sku.getImages()) {
					if (image.getDefaultImg() == 1) {
						defaultImg = image.getImgUrl();
					}
				}
				SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
				BeanUtils.copyProperties(sku, skuInfoEntity);
				skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
				skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
				skuInfoEntity.setSaleCount(0L);
				skuInfoEntity.setSpuId(spuInfoEntity.getId());
				skuInfoEntity.setSkuDefaultImg(defaultImg);
				skuInfoService.saveSkuInfo(skuInfoEntity);
				Long skuId = skuInfoEntity.getSkuId();

				// 6.2 保存 sku 图片集 pms_sku_images
				List<SkuImagesEntity> skuImagesEntities = sku.getImages().stream().map(img -> {
					SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
					skuImagesEntity.setSkuId(skuId);
					skuImagesEntity.setImgUrl(img.getImgUrl());
					skuImagesEntity.setDefaultImg(img.getDefaultImg());
					return skuImagesEntity;
				}).filter(item->{
					return !StringUtils.isEmpty(item.getImgUrl());
				}).collect(Collectors.toList());
				skuImagesService.saveBatch(skuImagesEntities);

				// 6.3 保存 sku 销售属性 pms_sku_sale_attr_value

				List<Attr> attr = sku.getAttr();
				List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(item -> {
					SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
					BeanUtils.copyProperties(item, skuSaleAttrValueEntity);
					skuSaleAttrValueEntity.setSkuId(skuId);
					return skuSaleAttrValueEntity;
				}).collect(Collectors.toList());
				skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

				// 6.4 保存 sku 优惠信息 gulimall-sms -> sms_sku_ladder/sms_sku_full_reduction/sms_member_price
				SkuReductionTo skuReductionTo = new SkuReductionTo();
				BeanUtils.copyProperties(sku, skuReductionTo);
				skuReductionTo.setSkuId(skuId);
				if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
					R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
					Integer code1 = (Integer) r1.get("code");
					if (!code1.equals(0)) {
						log.error("【couponFeignService】 返回错误: {}", r1);
					}
				}
			});
		}


	}

	@Override
	public PageUtils queryPageByCondtion(Map<String, Object> params) {
		QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			wrapper.and(o -> {
				o.eq("id", key).or().like("spu_name", key);
			});
		}
		String status = (String) params.get("status");
		if (!StringUtils.isEmpty(status)) {
			wrapper.eq("publish_status", status);
		}
		String brandId = (String) params.get("brandId");
		if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
			wrapper.eq("brand_id", brandId);
		}
		String catalogId = (String) params.get("catelogId");
		if (!StringUtils.isEmpty(catalogId)) {
			wrapper.eq("catalog_id", catalogId);
		}
		IPage<SpuInfoEntity> page = this.page(
				new Query<SpuInfoEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Override
	public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
		baseMapper.insert(spuInfoEntity);
	}


}