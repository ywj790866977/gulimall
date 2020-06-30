package com.yanlaoge.gulimall.order.vo;

import com.yanlaoge.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author rubyle
 */
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code;
}
