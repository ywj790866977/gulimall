package com.yanlaoge.gulimall.order.dao;

import com.yanlaoge.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author rubyle
 * @date 2020-05-13 15:41:34
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
