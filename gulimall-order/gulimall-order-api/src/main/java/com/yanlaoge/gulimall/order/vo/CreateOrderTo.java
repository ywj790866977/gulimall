package com.yanlaoge.gulimall.order.vo;

import com.yanlaoge.gulimall.order.entity.OrderEntity;
import com.yanlaoge.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author rubyle
 */
@Data
public class CreateOrderTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    private BigDecimal payPrice;
    private BigDecimal fare;

}
