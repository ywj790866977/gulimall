package com.yanlaoge.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.order.entity.OrderEntity;
import com.yanlaoge.gulimall.order.vo.OrderConfirmVo;
import com.yanlaoge.gulimall.order.vo.OrderSubmitVo;
import com.yanlaoge.gulimall.order.vo.SubmitOrderResponseVo;

import java.util.Map;

/**
 * 订单
 *
 * @author rubyle
 * @email besokuser@163.com
 * @date 2020-05-13 15:41:34
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 订单确认页数据
     *
     * @return vo
     */
    OrderConfirmVo confirmOrder();

    /**
     * 下单
     *
     * @param vo vo
     * @return order
     */
    SubmitOrderResponseVo orderSubmit(OrderSubmitVo vo);

}

