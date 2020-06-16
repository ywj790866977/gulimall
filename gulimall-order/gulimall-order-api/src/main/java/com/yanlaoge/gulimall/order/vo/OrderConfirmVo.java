package com.yanlaoge.gulimall.order.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 好人
 * @date 2020-06-14 13:17
 **/
@Data
public class OrderConfirmVo {
    /**
     * 地址
     */
    private List<MemberAddressVo> address;
    /**
     * 购物项
     */
    private List<OrderItemVo> items;
    /**
     * 优惠券
     */
    private Integer integration;
    /**
     * 总金额
     */
    private BigDecimal total;
    /**
     * 应付金额
     */
    private BigDecimal payPrice;
    /**
     * 订单防重令牌
     */
    private String orderToken;
    /**
     * 总数量
     */
    private Integer totalCount;

    public Integer getTotalCount() {
        if (!CollectionUtils.isEmpty(this.items)) {
            return this.items.stream().mapToInt(OrderItemVo::getCount).sum();
        }
        return totalCount;
    }

    //发票...


    public BigDecimal getTotal() {
        if(!CollectionUtils.isEmpty(this.items)){
            return this.items.stream().map(OrderItemVo::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return total;
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
