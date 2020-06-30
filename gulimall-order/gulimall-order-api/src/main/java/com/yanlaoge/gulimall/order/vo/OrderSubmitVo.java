package com.yanlaoge.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 好人
 * @date 2020-06-30 18:39
 **/
@Data
public class OrderSubmitVo {
    /**
     * 地址id
     */
    private Long addrId;
    /**
     * 支付类型
     */
    private Integer payType;
    /**
     * 防重token
     */
    private String orderToken;
    /**
     * 应付金额
     */
    private BigDecimal payPrice;
    /**
     * 备注
     */
    private String note;

    //用户信息在session里
    // 优惠,发票
    //购物车从redis获取;


}
