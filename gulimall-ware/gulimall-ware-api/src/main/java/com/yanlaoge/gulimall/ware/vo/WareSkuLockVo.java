package com.yanlaoge.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author  rubyle
 */
@Data
public class WareSkuLockVo {
    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 需要锁定的库存信息
     */
    private List<OrderItemLockVo> locks;
}
