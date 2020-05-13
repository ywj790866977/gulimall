package com.yanlaoge.gulimall.coupon.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanlaoge.gulimall.coupon.entity.CouponEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author rubyle
 * @date 2020-05-13 15:02:05
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
