package com.yanlaoge.gulimall.seckill.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 好人
 * @date 2020-07-11 17:24
 **/
@Data
public class SeckillOrderTo {
    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 购买数量
     */
    private Integer num;
    /**
     * 会员id
     */
    private Long memberId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
}
