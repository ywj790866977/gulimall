package com.yanlaoge.gulimall.cart.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author rubyle
 */
@Data
public class Cart {
    private List<CartItem> items;
    private Integer countNum;
    private Integer countType;
    private BigDecimal totalAmount;
    private BigDecimal reduce = BigDecimal.ZERO;

    /**
     * 获取商品数量
     *
     * @return 数量
     */
    public Integer getCountNum() {
        if (!CollectionUtils.isEmpty(this.items)) {
            return this.items.stream().mapToInt(CartItem::getCount).sum();
        }
        return countNum;
    }

    /**
     * 获取商品类型数量
     *
     * @return 数量
     */
    public Integer getCountType() {
        return items.size();
    }

    /**
     * 获取总金额
     *
     * @return 金额
     */
    public BigDecimal getTotalAmount() {
        if (!CollectionUtils.isEmpty(this.items)) {
            return this.items.stream().map(CartItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .subtract(getReduce());
        }
        return totalAmount;
    }

}
