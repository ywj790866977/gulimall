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
 * @date 2020-05-13 15:41:34
 */
public interface OrderService extends IService<OrderEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
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

    /**
     * 查询订单
     *
     * @param orderSn 订单号
     * @return 订单
     */
    OrderEntity getOrderByOrderSn(String orderSn);


    /**
     * 关闭订单
     *
     * @param orderEntity 订单实体
     */
    void closeOrder(OrderEntity orderEntity);
}

