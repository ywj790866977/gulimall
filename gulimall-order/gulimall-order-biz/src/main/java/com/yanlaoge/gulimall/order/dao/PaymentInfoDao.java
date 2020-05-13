package com.yanlaoge.gulimall.order.dao;

import com.yanlaoge.gulimall.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author rubyle
 * @email besokuser@163.com
 * @date 2020-05-13 15:41:34
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
