package com.yanlaoge.gulimall.coupon.service.impl;

import com.yanlaoge.common.to.MemberPrice;
import com.yanlaoge.common.to.SkuReductionTo;
import com.yanlaoge.gulimall.coupon.entity.MemberPriceEntity;
import com.yanlaoge.gulimall.coupon.entity.SkuLadderEntity;
import com.yanlaoge.gulimall.coupon.service.MemberPriceService;
import com.yanlaoge.gulimall.coupon.service.SkuLadderService;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;

import com.yanlaoge.gulimall.coupon.dao.SkuFullReductionDao;
import com.yanlaoge.gulimall.coupon.entity.SkuFullReductionEntity;
import com.yanlaoge.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

	@Resource
	private SkuLadderService skuLadderService;
	@Resource
	private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public void saveSkuReduction(SkuReductionTo skuReductionTo) {
		//1. 商品阶梯价格
		SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
		skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
		skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
		skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
		skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
		skuLadderService.save(skuLadderEntity);

		//2. 保存商品满减信息
		SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
		BeanUtils.copyProperties(skuReductionTo,skuFullReductionEntity);
		this.save(skuFullReductionEntity);

		//3. 商品会员价格
		List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
		List<MemberPriceEntity> collect = memberPrice.stream().map(item -> {
			MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
			memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
			memberPriceEntity.setMemberLevelId(item.getId());
			memberPriceEntity.setMemberLevelName(item.getName());
			memberPriceEntity.setAddOther(1);
			return memberPriceEntity;
		}).collect(Collectors.toList());
		memberPriceService.saveBatch(collect);
	}

}